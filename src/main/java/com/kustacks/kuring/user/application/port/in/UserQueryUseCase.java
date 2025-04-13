package com.kustacks.kuring.user.application.port.in;

import com.kustacks.kuring.user.application.port.in.dto.UserAIAskCountResult;
import com.kustacks.kuring.user.application.port.in.dto.UserBookmarkResult;
import com.kustacks.kuring.user.application.port.in.dto.UserCategoryNameResult;
import com.kustacks.kuring.user.application.port.in.dto.UserDepartmentNameResult;
import com.kustacks.kuring.user.application.port.in.dto.UserInfoResult;

import java.util.List;

public interface UserQueryUseCase {
    List<UserCategoryNameResult> lookupSubscribeCategories(String userToken);
    List<UserDepartmentNameResult> lookupSubscribeDepartments(String userToken);
    List<UserBookmarkResult> lookupUserBookmarkedNotices(String userToken);

    UserAIAskCountResult lookupUserAIAskCount(String userToken);
    UserInfoResult lookupUserInfo(String email);
}
