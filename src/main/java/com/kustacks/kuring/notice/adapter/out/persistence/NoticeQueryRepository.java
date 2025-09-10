package com.kustacks.kuring.notice.adapter.out.persistence;

import com.kustacks.kuring.notice.application.port.out.dto.NoticeDto;
import com.kustacks.kuring.notice.application.port.out.dto.NoticeSearchDto;
import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.user.application.port.out.dto.BookmarkDto;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

interface NoticeQueryRepository {

    List<NoticeDto> findNoticesByCategoryWithOffset(CategoryName categoryName, Pageable pageable);

    List<NoticeSearchDto> findAllByKeywords(List<String> containedNames);

    List<String> findNormalArticleIdsByCategory(CategoryName categoryName);

    void deleteAllByIdsAndCategory(CategoryName categoryName, List<String> articleIds);

    List<NoticeDto> findImportantNoticesByDepartment(DepartmentName departmentName, Boolean graduated);

    List<NoticeDto> findNormalNoticesByDepartmentWithOffset(DepartmentName departmentName, Boolean graduated, Pageable pageable);

    List<String> findImportantArticleIdsByCategoryName(CategoryName categoryName);

    List<String> findNormalArticleIdsByCategoryName(CategoryName categoryName);

    List<Integer> findImportantArticleIdsByDepartment(DepartmentName departmentNameEnum, Boolean graduated);

    List<Integer> findNormalArticleIdsByDepartment(DepartmentName departmentNameEnum, Boolean graduated);

    void deleteAllByIdsAndDepartment(DepartmentName departmentName, List<String> articleIds);

    List<BookmarkDto> findAllByBookmarkIds(List<String> ids);

    void changeNoticeImportantByArticleId(CategoryName categoryName, List<String> articleIds, boolean important);

    void updateNoticeEmbeddingStatus(List<String> articleIds, CategoryName categoryName);

    List<NoticeDto> findNotYetEmbeddingNoticeByDate(CategoryName categoryName, LocalDateTime date);

    Optional<NoticeDto> findNoticeReadModelByArticleId(Long id);
}
