package com.kustacks.kuring.user.business;

import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.user.common.dto.SubscribeCompareResultDto;
import com.kustacks.kuring.user.domain.User;
import com.kustacks.kuring.user.domain.UserRepository;
import com.kustacks.kuring.user.exception.UserNotFoundException;
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

    @Transactional(readOnly = true)
    public User getUserByToken(String token) {
        return userRepository.findByToken(token)
                .orElseThrow(UserNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<DepartmentName> lookupSubscribeDepartmentList(String id) {
        User findUser = findUserByToken(id);
        return findUser.getSubscribedDepartmentList();
    }

    public SubscribeCompareResultDto<DepartmentName> editSubscribeDepartmentList(String userToken, List<String> departments) {
        User user = findUserByToken(userToken);

        List<DepartmentName> newDepartmentNames = convertHostPrefixToEnum(departments);

        List<DepartmentName> savedDepartmentNames = user.filteringNewDepartmentName(newDepartmentNames);
        List<DepartmentName> deletedDepartmentNames = user.filteringOldDepartmentName(newDepartmentNames);
        return new SubscribeCompareResultDto<>(savedDepartmentNames, deletedDepartmentNames);
    }

    public void subscribeDepartment(String userToken, DepartmentName newDepartmentName) {
        User user = findUserByToken(userToken);
        user.subscribeDepartment(newDepartmentName);
    }

    public void unsubscribeDepartment(String userToken, DepartmentName removeDepartmentName) {
        User user = findUserByToken(userToken);
        user.unsubscribeDepartment(removeDepartmentName);
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
}
