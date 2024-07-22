package com.kustacks.kuring.notice.application.port.out;

import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.notice.domain.DepartmentNotice;
import com.kustacks.kuring.notice.domain.Notice;

import java.util.List;

public interface NoticeCommandPort {

    void saveAllCategoryNotices(List<Notice> notices);
    void saveAllDepartmentNotices(List<DepartmentNotice> departmentNotices);
    void deleteAllByIdsAndCategory(CategoryName categoryName, List<String> articleIds);
    void deleteAllByIdsAndDepartment(DepartmentName departmentName, List<String> articleIds);
    void changeNoticeImportantToFalseByArticleId(CategoryName categoryName, List<String> articleIds);
    void updateNoticeEmbeddingStatus(CategoryName categoryName, List<String> articleIds);
}
