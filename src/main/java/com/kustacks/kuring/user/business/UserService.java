package com.kustacks.kuring.user.business;

import com.kustacks.kuring.category.business.event.Events;
import com.kustacks.kuring.category.business.event.SubscribedRollbackEvent;
import com.kustacks.kuring.common.error.APIException;
import com.kustacks.kuring.common.error.ErrorCode;
import com.kustacks.kuring.common.firebase.FirebaseService;
import com.kustacks.kuring.common.firebase.exception.FirebaseSubscribeException;
import com.kustacks.kuring.common.firebase.exception.FirebaseUnSubscribeException;
import com.kustacks.kuring.notice.common.dto.DepartmentNameDto;
import com.kustacks.kuring.user.domain.User;
import com.kustacks.kuring.user.domain.UserRepository;
import com.kustacks.kuring.user.exception.UserNotFoundException;
import com.kustacks.kuring.worker.DepartmentName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FirebaseService firebaseService;

    @Transactional(readOnly = true)
    public User getUserByToken(String token) {
        return userRepository.findByToken(token)
                .orElseThrow(UserNotFoundException::new);
    }

    public List<DepartmentNameDto> lookupSubscribeDepartmentList(String id) {
        User findUser = findUserByToken(id);
        List<DepartmentName> departmentNameList = findUser.getSubscribedDepartmentList();
        return convertDepartmentDtoList(departmentNameList);
    }

    public void editSubscribeDepartmentList(String userToken, List<String> departments) {
        User user = findUserByToken(userToken);

        List<DepartmentName> newDepartmentNames = convertHostPrefixToEnum(departments);

        List<DepartmentName> savedDepartmentNames = user.filteringNewDepartmentName(newDepartmentNames);
        List<DepartmentName> deletedDepartmentNames = user.filteringOldDepartmentName(newDepartmentNames);
        editDepartmentNameList(user, savedDepartmentNames, deletedDepartmentNames);
    }

    private User findUserByToken(String token) {
        Optional<User> optionalUser = userRepository.findByToken(token);
        if (optionalUser.isEmpty()) {
            optionalUser = Optional.of(userRepository.save(new User(token)));
        }

        return optionalUser.orElseThrow(UserNotFoundException::new);
    }

    private List<DepartmentName> convertHostPrefixToEnum(List<String> departments) {
        return departments.stream()
                .map(DepartmentName::fromHostPrefix)
                .collect(Collectors.toList());
    }

    private void editDepartmentNameList(User user, List<DepartmentName> savedDepartmentNames, List<DepartmentName> deletedDepartmentNames) {
        try {
            SubscribedRollbackEvent subscribedRollbackEvent = new SubscribedRollbackEvent(user.getToken());
            Events.raise(subscribedRollbackEvent);

            subscribeDepartment(savedDepartmentNames, user, subscribedRollbackEvent);
            unsubscribeDepartment(deletedDepartmentNames, user, subscribedRollbackEvent);
        } catch (FirebaseSubscribeException | FirebaseUnSubscribeException e) {
            throw new APIException(ErrorCode.API_FB_CANNOT_EDIT_CATEGORY, e);
        }
    }

    private void subscribeDepartment(List<DepartmentName> newDepartmentNames, User user, SubscribedRollbackEvent subscribedRollbackEvent) {
        for (DepartmentName newDepartmentName : newDepartmentNames) {
            firebaseService.subscribe(user.getToken(), newDepartmentName.getName());
            user.subscribeDepartment(newDepartmentName);
            subscribedRollbackEvent.addNewCategoryName(newDepartmentName.getName());
            log.info("구독 성공 = {}", newDepartmentName.getName());
        }
    }

    private void unsubscribeDepartment(List<DepartmentName> removeDepartmentNames, User user, SubscribedRollbackEvent subscribedRollbackEvent) {
        for (DepartmentName removeDepartmentName : removeDepartmentNames) {
            firebaseService.unsubscribe(user.getToken(), removeDepartmentName.getName());
            user.unsubscribeDepartment(removeDepartmentName);
            subscribedRollbackEvent.deleteNewCategoryName(removeDepartmentName.getName());
            log.info("구독 취소 = {}", removeDepartmentName.getName());
        }
    }

    private List<DepartmentNameDto> convertDepartmentDtoList(List<DepartmentName> departmentNames) {
        return departmentNames.stream()
                .map(DepartmentNameDto::from)
                .collect(Collectors.toList());
    }
}
