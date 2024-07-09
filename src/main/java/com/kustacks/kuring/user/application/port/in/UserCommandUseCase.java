package com.kustacks.kuring.user.application.port.in;

import com.kustacks.kuring.user.application.port.in.dto.*;

public interface UserCommandUseCase {
    void editSubscribeCategories(UserCategoriesSubscribeCommand command);
    void editSubscribeDepartments(UserDepartmentsSubscribeCommand command);
    void saveFeedback(UserFeedbackCommand command);
    void saveBookmark(UserBookmarkCommand command);
    void decreaseQuestionCount(UserDecreaseQuestionCountCommand command);
}
