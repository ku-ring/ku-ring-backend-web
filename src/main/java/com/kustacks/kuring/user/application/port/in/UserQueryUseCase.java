package com.kustacks.kuring.user.application.port.in;

import com.kustacks.kuring.user.application.port.in.dto.*;

import java.util.List;

public interface UserQueryUseCase {
    List<UserCategoryNameResult> lookupSubscribeCategories(String userToken);
    List<UserDepartmentNameResult> lookupSubscribeDepartments(String userToken);
    List<UserBookmarkResult> lookupUserBookmarkedNotices(String userToken);

    UserAIAskCountResult lookupUserAIAskCountWithFcmToken(String userToken);
    UserAIAskCountResult lookupUserAIAskCountWithEmail(String email);
    UserInfoResult lookupUserInfo(String email);

    void checkUserAskAvailability(String userToken, String email);
}
