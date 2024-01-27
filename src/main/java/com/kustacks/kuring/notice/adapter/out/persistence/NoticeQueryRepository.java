package com.kustacks.kuring.notice.adapter.out.persistence;

import com.kustacks.kuring.notice.application.port.out.dto.NoticeDto;
import com.kustacks.kuring.notice.application.port.out.dto.NoticeSearchDto;
import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.user.application.port.out.dto.BookmarkDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

interface NoticeQueryRepository {

    List<NoticeDto> findNoticesByCategoryWithOffset(CategoryName categoryName, Pageable pageable);

    List<NoticeSearchDto> findAllByKeywords(List<String> containedNames);

    List<String> findNormalArticleIdsByCategory(CategoryName categoryName);

    void deleteAllByIdsAndCategory(CategoryName categoryName, List<String> articleIds);

    List<NoticeDto> findImportantNoticesByDepartment(DepartmentName departmentName);

    List<NoticeDto> findNormalNoticesByDepartmentWithOffset(DepartmentName departmentName, Pageable pageable);

    List<Integer> findImportantArticleIdsByDepartment(DepartmentName departmentNameEnum);

    List<Integer> findNormalArticleIdsByDepartment(DepartmentName departmentNameEnum);

    void deleteAllByIdsAndDepartment(DepartmentName departmentName, List<String> articleIds);

    List<BookmarkDto> findAllByBookmarkIds(List<String> ids);
}
