package com.kustacks.kuring.worker.update.notice;

import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.notice.domain.DepartmentNotice;
import com.kustacks.kuring.notice.domain.Notice;
import com.kustacks.kuring.worker.update.notice.dto.response.CommonNoticeFormatDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Component
public class NoticeUpdateSupport {

    public List<Notice> filteringSoonSaveNotices(
            List<CommonNoticeFormatDto> scrapResults,
            List<String> savedArticleIds,
            CategoryName categoryName,
            boolean important
    ) {
        List<Notice> newNotices = new LinkedList<>(); // 뒤에 추가만 계속 하기 때문에 arrayList가 아닌 Linked List 사용 O(1)
        for (CommonNoticeFormatDto notice : scrapResults) {
            try {
                if (Collections.binarySearch(savedArticleIds, notice.getArticleId()) < 0) { // 정렬되어있다, 이진탐색으로 O(logN)안에 수행
                    Notice newNotice = convert(notice, categoryName, important);
                    newNotices.add(newNotice);
                }
            } catch (IncorrectResultSizeDataAccessException e) {
                log.warn("오류가 발생한 공지 정보");
                log.warn("articleId = {}", notice.getArticleId());
                log.warn("postedDate = {}", notice.getPostedDate());
                log.warn("subject = {}", notice.getSubject());
            }
        }

        return newNotices;
    }

    public List<Notice> filteringSoonSaveNotices(
            List<CommonNoticeFormatDto> scrapResults,
            List<String> savedArticleIds,
            CategoryName categoryName
    ) {
        return this.filteringSoonSaveNotices(scrapResults, savedArticleIds, categoryName, false);
    }

    public List<DepartmentNotice> filteringSoonSaveDepartmentNotices(
            List<CommonNoticeFormatDto> scrapResults,
            List<Integer> savedArticleIds,
            DepartmentName departmentNameEnum,
            boolean important,
            boolean graduated
    ) {
        List<DepartmentNotice> newNotices = new LinkedList<>(); // 뒤에 추가만 계속 하기 때문에 arrayList가 아닌 Linked List 사용 O(1)
        for (CommonNoticeFormatDto notice : scrapResults) {
            try {
                if (Collections.binarySearch(savedArticleIds, Integer.valueOf(notice.getArticleId())) < 0) { // 정렬되어있다, 이진탐색으로 O(logN)안에 수행
                    DepartmentNotice newDepartmentNotice = convert(notice, departmentNameEnum, important, graduated);
                    newNotices.add(newDepartmentNotice);
                }
            } catch (IncorrectResultSizeDataAccessException e) {
                log.warn("오류가 발생한 공지 정보");
                log.warn("articleId = {}", notice.getArticleId());
                log.warn("postedDate = {}", notice.getPostedDate());
                log.warn("subject = {}", notice.getSubject());
            }
        }
        return newNotices;
    }

    // Notice의 id는 문자열 이지만, DepartmentNotice의 id는 숫자만 가능하다,
    // 따라서 매우 유사한 로직임에도 불구하고 별도의 메서드로 만든다.
    public List<String> extractNoticeIds(List<CommonNoticeFormatDto> scrapResults) {
        return scrapResults.stream()
                .map(CommonNoticeFormatDto::getArticleId)
                .sorted()
                .toList();
    }

    public List<Integer> extractDepartmentNoticeIds(List<CommonNoticeFormatDto> scrapResults) {
        return scrapResults.stream()
                .map(scrap -> Integer.parseInt(scrap.getArticleId()))
                .sorted()
                .toList();
    }

    public List<String> filteringSoonDeleteNoticeIds(
            List<String> savedArticleIds,
            List<String> latestNoticeIds
    ) {
        return savedArticleIds.stream()
                .filter(savedArticleId -> Collections.binarySearch(latestNoticeIds, savedArticleId) < 0)
                .map(Object::toString)
                .toList();
    }

    public List<String> filteringSoonDeleteDepartmentNoticeIds(
            List<Integer> savedArticleIds,
            List<Integer> latestNoticeIds
    ) {
        return savedArticleIds.stream()
                .filter(savedArticleId -> Collections.binarySearch(latestNoticeIds, savedArticleId) < 0)
                .map(Object::toString)
                .toList();
    }

    private Notice convert(CommonNoticeFormatDto dto, CategoryName CategoryName, boolean important) {
        return new Notice(dto.getArticleId(),
                dto.getPostedDate(),
                dto.getUpdatedDate(),
                dto.getSubject(),
                CategoryName,
                important,
                dto.getFullUrl());
    }

    private DepartmentNotice convert(CommonNoticeFormatDto dto, DepartmentName departmentNameEnum, boolean important, boolean graduated) {
        return DepartmentNotice.builder()
                .articleId(dto.getArticleId())
                .postedDate(dto.getPostedDate())
                .updatedDate(dto.getUpdatedDate())
                .subject(dto.getSubject())
                .fullUrl(dto.getFullUrl())
                .important(important)
                .categoryName(CategoryName.DEPARTMENT)
                .departmentName(departmentNameEnum)
                .graduated(graduated)
                .build();
    }
}
