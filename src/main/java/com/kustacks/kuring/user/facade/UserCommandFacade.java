package com.kustacks.kuring.user.facade;

import com.kustacks.kuring.category.business.CategoryService;
import com.kustacks.kuring.category.business.event.Events;
import com.kustacks.kuring.category.business.event.SubscribedRollbackEvent;
import com.kustacks.kuring.common.error.APIException;
import com.kustacks.kuring.common.error.ErrorCode;
import com.kustacks.kuring.common.firebase.FirebaseService;
import com.kustacks.kuring.common.firebase.exception.FirebaseSubscribeException;
import com.kustacks.kuring.common.firebase.exception.FirebaseUnSubscribeException;
import com.kustacks.kuring.feedback.business.FeedbackService;
import com.kustacks.kuring.user.business.UserService;
import com.kustacks.kuring.user.domain.UserCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserCommandFacade {

    private static final String NEW_CATEGORY_FLAG = "new";
    private static final String REMOVE_CATEGORY_FLAG = "remove";

    private final UserService userService;
    private final CategoryService categoryService;
    private final FirebaseService firebaseService;
    private final FeedbackService feedbackService;

    public void editSubscribeCategories(String userToken, List<String> newCategoryNames) {
        firebaseService.validationToken(userToken);
        Map<String, List<UserCategory>> compareResults = categoryService.compareSubscribeCategory(userToken, newCategoryNames);
        editUserCategoryList(userToken, compareResults);
    }

    public void editSubscribeDepartments(String userToken, List<String> departments) {
        firebaseService.validationToken(userToken);
        userService.editSubscribeDepartmentList(userToken, departments);
    }

    public void saveFeedback(String userToken, String feedback) {
        firebaseService.validationToken(userToken);
        feedbackService.saveFeedback(userToken, feedback);
    }

    private void editUserCategoryList(String userToken, Map<String, List<UserCategory>> compareResult) {
        try {
            SubscribedRollbackEvent subscribedRollbackEvent = new SubscribedRollbackEvent(userToken);
            Events.raise(subscribedRollbackEvent);

            subscribeUserCategory(compareResult, userToken, subscribedRollbackEvent);
            unsubscribeUserCategory(compareResult, userToken, subscribedRollbackEvent);
        } catch (FirebaseSubscribeException | FirebaseUnSubscribeException e) {
            throw new APIException(ErrorCode.API_FB_CANNOT_EDIT_CATEGORY, e);
        }
    }

    private void subscribeUserCategory(Map<String, List<UserCategory>> compareResult, String token, SubscribedRollbackEvent subscribedRollbackEvent) {
        List<UserCategory> newUserCategories = compareResult.get(NEW_CATEGORY_FLAG);
        for (UserCategory newUserCategory : newUserCategories) {
            firebaseService.subscribe(token, newUserCategory.getCategoryName());
            categoryService.saveUserCategory(newUserCategory);
            subscribedRollbackEvent.addNewCategoryName(newUserCategory.getCategoryName());
            log.info("구독 성공 = {}", newUserCategory.getCategoryName());
        }
    }

    private void unsubscribeUserCategory(Map<String, List<UserCategory>> compareResult, String token, SubscribedRollbackEvent subscribedRollbackEvent) {
        List<UserCategory> removeUserCategories = compareResult.get(REMOVE_CATEGORY_FLAG);
        for (UserCategory removeUserCategory : removeUserCategories) {
            firebaseService.unsubscribe(token, removeUserCategory.getCategoryName());
            categoryService.deleteUserCategory(removeUserCategory);
            subscribedRollbackEvent.deleteNewCategoryName(removeUserCategory.getCategoryName());
            log.info("구독 취소 = {}", removeUserCategory.getCategoryName());
        }
    }
}
