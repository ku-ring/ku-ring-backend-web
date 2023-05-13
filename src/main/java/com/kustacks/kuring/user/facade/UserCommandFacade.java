package com.kustacks.kuring.user.facade;

import com.kustacks.kuring.category.business.CategoryService;
import com.kustacks.kuring.common.firebase.FirebaseService;
import com.kustacks.kuring.feedback.business.FeedbackService;
import com.kustacks.kuring.user.business.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserCommandFacade {

    private final UserService userService;
    private final CategoryService categoryService;
    private final FirebaseService firebaseService;
    private final FeedbackService feedbackService;

    public void editSubscribeCategories(String userToken, List<String> newCategoryNames) {
        firebaseService.validationToken(userToken);
        categoryService.editSubscribeCategoryList(userToken, newCategoryNames);
    }

    public void editSubscribeDepartments(String userToken, List<String> departments) {
        firebaseService.validationToken(userToken);
        userService.editSubscribeDepartmentList(userToken, departments);
    }

    public void saveFeedback(String userToken, String feedback) {
        firebaseService.validationToken(userToken);
        feedbackService.saveFeedback(userToken, feedback);
    }
}
