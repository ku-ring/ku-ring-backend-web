package com.kustacks.kuring.notice.application.port.out;

import com.kustacks.kuring.notice.application.port.out.dto.NoticeDto;
import com.kustacks.kuring.notice.application.port.out.dto.NoticeSearchDto;
import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.user.application.port.out.dto.BookmarkDto;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NoticeQueryPort {

    List<NoticeDto> findNoticesByCategoryWithOffset(CategoryName categoryName, Pageable pageable);

    List<NoticeSearchDto> findAllByKeywords(List<String> containedNames);

    List<String> findNormalArticleIdsByCategory(CategoryName categoryName);

    List<NoticeDto> findImportantNoticesByDepartment(DepartmentName departmentName);

    List<NoticeDto> findNormalNoticesByDepartmentWithOffset(DepartmentName departmentName, Pageable pageable);

    List<String> findImportantArticleIdsByCategoryName(CategoryName categoryName);

    List<String> findNormalArticleIdsByCategoryName(CategoryName categoryName);

    List<Integer> findImportantArticleIdsByDepartment(DepartmentName departmentNameEnum);

    List<Integer> findNormalArticleIdsByDepartment(DepartmentName departmentNameEnum);

    List<BookmarkDto> findAllByBookmarkIds(List<String> ids);

    Long count();

    List<NoticeDto> findNotYetEmbeddingNotice(CategoryName categoryName, LocalDateTime now);

    Optional<NoticeDto> findNoticeById(Long id);
}
