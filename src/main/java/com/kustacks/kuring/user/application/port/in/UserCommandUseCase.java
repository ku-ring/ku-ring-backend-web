package com.kustacks.kuring.user.application.port.in;

import com.kustacks.kuring.user.application.port.in.dto.UserBookmarkCommand;
import com.kustacks.kuring.user.application.port.in.dto.UserCategoriesSubscribeCommand;
import com.kustacks.kuring.user.application.port.in.dto.UserDecreaseQuestionCountCommand;
import com.kustacks.kuring.user.application.port.in.dto.UserDepartmentsSubscribeCommand;
import com.kustacks.kuring.user.application.port.in.dto.UserFeedbackCommand;
import com.kustacks.kuring.user.application.port.in.dto.UserLoginCommand;
import com.kustacks.kuring.user.application.port.in.dto.UserLoginResult;
import com.kustacks.kuring.user.application.port.in.dto.UserLogoutCommand;
import com.kustacks.kuring.user.application.port.in.dto.UserPasswordModifyCommand;
import com.kustacks.kuring.user.application.port.in.dto.UserSignupCommand;
import com.kustacks.kuring.user.application.port.in.dto.UserWithdrawCommand;

public interface UserCommandUseCase {
    void editSubscribeCategories(UserCategoriesSubscribeCommand command);
    void editSubscribeDepartments(UserDepartmentsSubscribeCommand command);
    void saveFeedback(UserFeedbackCommand command);
    void saveBookmark(UserBookmarkCommand command);
    void decreaseQuestionCount(UserDecreaseQuestionCountCommand command);
    void signupUser(UserSignupCommand userSignupCommand);
    void logout(UserLogoutCommand userLogoutCommand);
    UserLoginResult login(UserLoginCommand userLoginCommand);
    void withdraw(UserWithdrawCommand userWithdrawCommand);
    void changePassword(UserPasswordModifyCommand userPasswordModifyCommand);
}
