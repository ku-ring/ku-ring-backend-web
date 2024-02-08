package com.kustacks.kuring.notice.application.port.in;

import com.kustacks.kuring.notice.application.port.in.dto.*;

import java.util.List;

public interface NoticeQueryUseCase {
    List<NoticeRangeLookupResult> getNotices(NoticeRangeLookupCommand command);
    List<NoticeContentSearchResult> findAllNoticeByContent(String content);
    List<NoticeCategoryNameResult> lookupSupportedCategories();
    List<NoticeDepartmentNameResult> lookupSupportedDepartments();
}
