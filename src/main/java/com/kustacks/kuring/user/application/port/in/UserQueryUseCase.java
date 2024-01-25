package com.kustacks.kuring.user.application.port.in;

import com.kustacks.kuring.user.application.port.in.dto.AdminFeedbacksResult;
import com.kustacks.kuring.user.application.port.in.dto.UserBookmarkResult;
import com.kustacks.kuring.user.application.port.in.dto.UserCategoryNameResult;
import com.kustacks.kuring.user.application.port.in.dto.UserDepartmentNameResult;

import java.util.List;

public interface UserQueryUseCase {
    List<UserCategoryNameResult> lookupSubscribeCategories(String userToken);
    List<UserDepartmentNameResult> lookupSubscribeDepartments(String userToken);

    // TODO : 향후 Admin쪽으로 이전 예정
    List<AdminFeedbacksResult> lookupFeedbacks(int page, int size);
    List<UserBookmarkResult> lookupUserBookmarkedNotices(String userToken);
}
