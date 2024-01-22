package com.kustacks.kuring.user.business;

import com.kustacks.kuring.admin.common.dto.FeedbackDto;
import com.kustacks.kuring.common.exception.NotFoundException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.message.firebase.FirebaseService;
import com.kustacks.kuring.message.firebase.ServerProperties;
import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.user.common.dto.SubscribeCompareResultDto;
import com.kustacks.kuring.user.domain.User;
import com.kustacks.kuring.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.kustacks.kuring.message.firebase.FirebaseService.ALL_DEVICE_SUBSCRIBED_TOPIC;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FirebaseService firebaseService;
    private final ServerProperties serverProperties;

    @Transactional(readOnly = true)
    public User getUserByToken(String token) {
        return userRepository.findByToken(token)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<DepartmentName> lookupSubscribeDepartmentList(String id) {
        User findUser = findUserByToken(id);
        return findUser.getSubscribedDepartmentList();
    }

    @Transactional(readOnly = true)
    public List<CategoryName> lookUpUserCategories(String token) {
        User findUser = findUserByToken(token);
        return findUser.getSubscribedCategoryList();
    }

    @Transactional(readOnly = true)
    public List<FeedbackDto> lookupFeedbacks(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return userRepository.findAllFeedbackByPageRequest(pageRequest);
    }

    public void saveFeedback(String token, String content) {
        User findUser = findUserByToken(token);
        findUser.addFeedback(content);
    }

    public SubscribeCompareResultDto<CategoryName> editSubscribeCategoryList(
            String userToken, List<String> newCategoryStringNames) {
        User user = findUserByToken(userToken);

        List<CategoryName> newCategoryNames = convertToEnumList(newCategoryStringNames);

        List<CategoryName> savedCategoryNames = user.filteringNewCategoryName(newCategoryNames);
        List<CategoryName> deletedCategoryNames = user.filteringOldCategoryName(newCategoryNames);

        return new SubscribeCompareResultDto<>(savedCategoryNames, deletedCategoryNames);
    }

    public void subscribeCategory(String userToken, CategoryName categoryName) {
        User user = findUserByToken(userToken);
        user.subscribeCategory(categoryName);
    }

    public void unsubscribeCategory(String userToken, CategoryName categoryName) {
        User user = findUserByToken(userToken);
        user.unsubscribeCategory(categoryName);
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
            firebaseService.subscribe(token, serverProperties.ifDevThenAddSuffix(ALL_DEVICE_SUBSCRIBED_TOPIC));
        }

        return optionalUser.orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    private List<CategoryName> convertToEnumList(List<String> categories) {
        return categories.stream()
                .map(CategoryName::fromStringName)
                .toList();
    }

    private List<DepartmentName> convertHostPrefixToEnum(List<String> departments) {
        return departments.stream()
                .map(DepartmentName::fromHostPrefix)
                .toList();
    }
}
