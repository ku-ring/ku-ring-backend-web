package com.kustacks.kuring.user.application.service;

import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.common.exception.NotFoundException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.message.firebase.FirebaseService;
import com.kustacks.kuring.message.firebase.ServerProperties;
import com.kustacks.kuring.message.firebase.exception.FirebaseSubscribeException;
import com.kustacks.kuring.message.firebase.exception.FirebaseUnSubscribeException;
import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.user.adapter.out.persistence.UserPersistenceAdapter;
import com.kustacks.kuring.user.application.port.in.UserCommandUseCase;
import com.kustacks.kuring.user.application.port.in.dto.*;
import com.kustacks.kuring.user.domain.User;
import com.kustacks.kuring.worker.event.Events;
import com.kustacks.kuring.worker.event.SubscribedRollbackEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.kustacks.kuring.message.firebase.FirebaseService.ALL_DEVICE_SUBSCRIBED_TOPIC;

@Slf4j
@UseCase
@Transactional
@RequiredArgsConstructor
class UserCommandService implements UserCommandUseCase {

    private final UserPersistenceAdapter userPersistenceAdapter;
    private final FirebaseService firebaseService;
    private final ServerProperties serverProperties;

    @Override
    public void editSubscribeCategories(UserCategoriesSubscribeCommand command) {
        firebaseService.validationToken(command.userToken());

        UserSubscribeCompareResult<CategoryName> compareResults =
                this.editSubscribeCategoryList(command.userToken(), command.categories());

        editUserCategoryList(
                command.userToken(),
                compareResults.savedNameList(),
                compareResults.deletedNameList()
        );
    }

    @Override
    public void editSubscribeDepartments(UserDepartmentsSubscribeCommand command) {
        firebaseService.validationToken(command.userToken());

        UserSubscribeCompareResult<DepartmentName> compareResults
                = this.editSubscribeDepartmentList(command.userToken(), command.departments());

        editDepartmentNameList(
                command.userToken(),
                compareResults.savedNameList(),
                compareResults.deletedNameList()
        );
    }

    @Override
    public void saveFeedback(UserFeedbackCommand command) {
        firebaseService.validationToken(command.userToken());
        User findUser = findUserByToken(command.userToken());
        findUser.addFeedback(command.content());
    }

    @Override
    public void saveBookmark(UserBookmarkCommand command) {
        firebaseService.validationToken(command.userToken());
        User user = findUserByToken(command.userToken());
        user.addBookmark(command.articleId());
    }

    private UserSubscribeCompareResult<CategoryName> editSubscribeCategoryList(
            String userToken,
            List<String> newCategoryStringNames
    ) {
        User user = findUserByToken(userToken);

        List<CategoryName> newCategoryNames = convertToEnumList(newCategoryStringNames);

        List<CategoryName> savedCategoryNames = user.filteringNewCategoryName(newCategoryNames);
        List<CategoryName> deletedCategoryNames = user.filteringOldCategoryName(newCategoryNames);

        return new UserSubscribeCompareResult<>(savedCategoryNames, deletedCategoryNames);
    }

    private void editUserCategoryList(
            String userToken,
            List<CategoryName> savedCategoryNames,
            List<CategoryName> deletedCategoryNames
    ) throws FirebaseSubscribeException, FirebaseUnSubscribeException {
        SubscribedRollbackEvent subscribedRollbackEvent = new SubscribedRollbackEvent(userToken);
        Events.raise(subscribedRollbackEvent);

        subscribeUserCategory(userToken, savedCategoryNames, subscribedRollbackEvent);
        unsubscribeUserCategory(userToken, deletedCategoryNames, subscribedRollbackEvent);
    }

    private UserSubscribeCompareResult<DepartmentName> editSubscribeDepartmentList(
            String userToken,
            List<String> departments
    ) {
        User user = findUserByToken(userToken);

        List<DepartmentName> newDepartmentNames = convertHostPrefixToEnum(departments);

        List<DepartmentName> savedDepartmentNames = user.filteringNewDepartmentName(newDepartmentNames);
        List<DepartmentName> deletedDepartmentNames = user.filteringOldDepartmentName(newDepartmentNames);
        return new UserSubscribeCompareResult<>(savedDepartmentNames, deletedDepartmentNames);
    }

    private void subscribeUserCategory(
            String token,
            List<CategoryName> newCategoryNames,
            SubscribedRollbackEvent subscribedRollbackEvent
    ) throws FirebaseSubscribeException {
        for (CategoryName newCategoryName : newCategoryNames) {
            firebaseService.subscribe(token, newCategoryName.getName());
            this.subscribeCategory(token, newCategoryName);
            subscribedRollbackEvent.addNewCategoryName(newCategoryName.getName());
            log.info("구독 성공 = {}", newCategoryName.getName());
        }
    }

    private void unsubscribeUserCategory(
            String token,
            List<CategoryName> removeCategoryNames,
            SubscribedRollbackEvent subscribedRollbackEvent
    ) throws FirebaseUnSubscribeException {
        for (CategoryName removeCategoryName : removeCategoryNames) {
            firebaseService.unsubscribe(token, removeCategoryName.getName());
            this.unsubscribeCategory(token, removeCategoryName);
            subscribedRollbackEvent.deleteNewCategoryName(removeCategoryName.getName());
            log.info("구독 취소 = {}", removeCategoryName.getName());
        }
    }

    private void editDepartmentNameList(
            String userToken,
            List<DepartmentName> savedDepartmentNames,
            List<DepartmentName> deletedDepartmentNames
    ) throws FirebaseSubscribeException, FirebaseUnSubscribeException {
        SubscribedRollbackEvent subscribedRollbackEvent = new SubscribedRollbackEvent(userToken);
        Events.raise(subscribedRollbackEvent);

        subscribeDepartment(userToken, savedDepartmentNames, subscribedRollbackEvent);
        unsubscribeDepartment(userToken, deletedDepartmentNames, subscribedRollbackEvent);
    }

    private void subscribeCategory(String userToken, CategoryName categoryName) {
        User user = findUserByToken(userToken);
        user.subscribeCategory(categoryName);
    }

    private void unsubscribeCategory(String userToken, CategoryName categoryName) {
        User user = findUserByToken(userToken);
        user.unsubscribeCategory(categoryName);
    }

    private void subscribeDepartment(
            String userToken,
            List<DepartmentName> newDepartmentNames,
            SubscribedRollbackEvent subscribedRollbackEvent
    ) {
        for (DepartmentName newDepartmentName : newDepartmentNames) {
            firebaseService.subscribe(userToken, newDepartmentName.getName());
            this.subscribeDepartment(userToken, newDepartmentName);
            subscribedRollbackEvent.addNewCategoryName(newDepartmentName.getName());
            log.info("구독 성공 = {}", newDepartmentName.getName());
        }
    }

    private void unsubscribeDepartment(
            String userToken,
            List<DepartmentName> removeDepartmentNames,
            SubscribedRollbackEvent subscribedRollbackEvent
    ) {
        for (DepartmentName removeDepartmentName : removeDepartmentNames) {
            firebaseService.unsubscribe(userToken, removeDepartmentName.getName());
            this.unsubscribeDepartment(userToken, removeDepartmentName);
            subscribedRollbackEvent.deleteNewCategoryName(removeDepartmentName.getName());
            log.info("구독 취소 = {}", removeDepartmentName.getName());
        }
    }

    private void subscribeDepartment(String userToken, DepartmentName newDepartmentName) {
        User user = findUserByToken(userToken);
        user.subscribeDepartment(newDepartmentName);
    }

    private void unsubscribeDepartment(String userToken, DepartmentName removeDepartmentName) {
        User user = findUserByToken(userToken);
        user.unsubscribeDepartment(removeDepartmentName);
    }

    private User findUserByToken(String token) {
        Optional<User> optionalUser = userPersistenceAdapter.findByToken(token);
        if (optionalUser.isEmpty()) {
            optionalUser = Optional.of(userPersistenceAdapter.save(new User(token)));
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
