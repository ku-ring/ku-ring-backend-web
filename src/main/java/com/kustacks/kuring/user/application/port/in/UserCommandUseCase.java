package com.kustacks.kuring.user.application.port.in;

import com.kustacks.kuring.user.application.port.in.dto.UserBookmarkCommand;
import com.kustacks.kuring.user.application.port.in.dto.UserFeedbackCommand;
import com.kustacks.kuring.user.application.port.in.dto.UserCategoriesSubscribeCommand;
import com.kustacks.kuring.user.application.port.in.dto.UserDepartmentsSubscribeCommand;

public interface UserCommandUseCase {
    void editSubscribeCategories(UserCategoriesSubscribeCommand command);
    void editSubscribeDepartments(UserDepartmentsSubscribeCommand command);
    void saveFeedback(UserFeedbackCommand command);
    void saveBookmark(UserBookmarkCommand command);
}
