package com.kustacks.kuring.notice.adapter.out.persistence;

import com.kustacks.kuring.common.annotation.PersistenceAdapter;
import com.kustacks.kuring.notice.application.port.out.NoticeCommandPort;
import com.kustacks.kuring.notice.application.port.out.NoticeQueryPort;
import com.kustacks.kuring.notice.application.port.out.dto.NoticeDto;
import com.kustacks.kuring.notice.application.port.out.dto.NoticeSearchDto;
import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.notice.domain.DepartmentNotice;
import com.kustacks.kuring.notice.domain.Notice;
import com.kustacks.kuring.user.application.port.out.dto.BookmarkDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

@PersistenceAdapter
@RequiredArgsConstructor
public class NoticePersistenceAdapter implements NoticeCommandPort, NoticeQueryPort {

    private final NoticeRepository noticeRepository;
    private final NoticeJdbcRepository noticeJdbcRepository;

    @Override
    public void saveAllCategoryNotices(List<Notice> notices) {
        this.noticeJdbcRepository.saveAllCategoryNotices(notices);
    }

    @Override
    public void saveAllDepartmentNotices(List<DepartmentNotice> departmentNotices) {
        this.noticeJdbcRepository.saveAllDepartmentNotices(departmentNotices);
    }

    @Override
    public void deleteAllByIdsAndCategory(CategoryName categoryName, List<String> articleIds) {
        this.noticeRepository.deleteAllByIdsAndCategory(categoryName, articleIds);
    }

    @Override
    public void deleteAllByIdsAndDepartment(DepartmentName departmentName, List<String> articleIds) {
        this.noticeRepository.deleteAllByIdsAndDepartment(departmentName, articleIds);
    }

    @Override
    public List<NoticeDto> findNoticesByCategoryWithOffset(CategoryName categoryName, Pageable pageable) {
        return this.noticeRepository.findNoticesByCategoryWithOffset(categoryName, pageable);
    }

    @Override
    public List<NoticeSearchDto> findAllByKeywords(List<String> containedNames) {
        return this.noticeRepository.findAllByKeywords(containedNames);
    }

    @Override
    public List<String> findNormalArticleIdsByCategory(CategoryName categoryName) {
        return this.noticeRepository.findNormalArticleIdsByCategory(categoryName);
    }

    @Override
    public List<NoticeDto> findImportantNoticesByDepartment(DepartmentName departmentName) {
        return this.noticeRepository.findImportantNoticesByDepartment(departmentName);
    }

    @Override
    public List<NoticeDto> findNormalNoticesByDepartmentWithOffset(DepartmentName departmentName, Pageable pageable) {
        return this.noticeRepository.findNormalNoticesByDepartmentWithOffset(departmentName, pageable);
    }

    @Override
    public List<String> findImportantArticleIdsByCategoryName(CategoryName categoryName) {
        return this.noticeRepository.findImportantArticleIdsByCategoryName(categoryName);
    }

    @Override
    public List<String> findNormalArticleIdsByCategoryName(CategoryName categoryName) {
        return this.noticeRepository.findNormalArticleIdsByCategoryName(categoryName);
    }

    @Override
    public List<Integer> findImportantArticleIdsByDepartment(DepartmentName departmentNameEnum) {
        return this.noticeRepository.findImportantArticleIdsByDepartment(departmentNameEnum);
    }

    @Override
    public List<Integer> findNormalArticleIdsByDepartment(DepartmentName departmentNameEnum) {
        return this.noticeRepository.findNormalArticleIdsByDepartment(departmentNameEnum);
    }

    @Override
    public List<BookmarkDto> findAllByBookmarkIds(List<String> ids) {
        return this.noticeRepository.findAllByBookmarkIds(ids);
    }

    @Override
    public Long count() {
        return this.noticeRepository.count();
    }
}
