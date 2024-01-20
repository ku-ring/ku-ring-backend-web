package com.kustacks.kuring.worker.update.notice;

import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.notice.domain.DepartmentNotice;
import com.kustacks.kuring.notice.domain.Notice;
import com.kustacks.kuring.worker.update.notice.dto.response.CommonNoticeFormatDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NoticeUpdateSupportTest {

    private NoticeUpdateSupport noticeUpdateSupport;

    @BeforeEach
    void setUp() {
        noticeUpdateSupport = new NoticeUpdateSupport();
    }

    @DisplayName("이미 저장되있는 일반 공지의 경우 중복 저장하지 않도록 걸러낸다")
    @Test
    public void filteringSoonSaveNotices() {
        // given
        List<CommonNoticeFormatDto> originNotices = new ArrayList<>();

        CommonNoticeFormatDto notice1 = CommonNoticeFormatDto.builder().articleId("1").updatedDate("2021-01-01").subject("library1")
                .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71921").important(false).build();
        CommonNoticeFormatDto notice2 = CommonNoticeFormatDto.builder().articleId("2").updatedDate("2021-01-01").subject("library1")
                .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71921").important(false).build();
        CommonNoticeFormatDto notice3 = CommonNoticeFormatDto.builder().articleId("3").updatedDate("2021-01-01").subject("library1")
                .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71921").important(false).build();
        CommonNoticeFormatDto notice4 = CommonNoticeFormatDto.builder().articleId("4").updatedDate("2021-01-01").subject("library1")
                .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71921").important(false).build();
        CommonNoticeFormatDto notice5 = CommonNoticeFormatDto.builder().articleId("5").updatedDate("2021-01-01").subject("library1")
                .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71921").important(false).build();

        originNotices.addAll(List.of(notice1, notice2, notice3, notice4, notice5));

        // when
        List<Notice> results = noticeUpdateSupport.filteringSoonSaveNotices(originNotices, List.of("1", "4"), CategoryName.LIBRARY);

        // then
        assertThat(results).hasSize(3)
                .extracting("articleId")
                .containsExactly("2", "3", "5");
    }

    @DisplayName("이미 저장되있는 학과별 공지의 경우 중복 저장하지 않도록 걸러낸다")
    @Test
    public void filteringSoonSaveDepartmentNotices() {
        // given
        List<CommonNoticeFormatDto> originNotices = new ArrayList<>();

        CommonNoticeFormatDto notice1 = CommonNoticeFormatDto.builder().articleId("1").updatedDate("2021-01-01").subject("library1")
                .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71921").important(false).build();
        CommonNoticeFormatDto notice2 = CommonNoticeFormatDto.builder().articleId("2").updatedDate("2021-01-01").subject("library1")
                .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71921").important(false).build();
        CommonNoticeFormatDto notice3 = CommonNoticeFormatDto.builder().articleId("3").updatedDate("2021-01-01").subject("library1")
                .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71921").important(false).build();
        CommonNoticeFormatDto notice4 = CommonNoticeFormatDto.builder().articleId("4").updatedDate("2021-01-01").subject("library1")
                .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71921").important(false).build();
        CommonNoticeFormatDto notice5 = CommonNoticeFormatDto.builder().articleId("5").updatedDate("2021-01-01").subject("library1")
                .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71921").important(false).build();

        originNotices.addAll(List.of(notice1, notice2, notice3, notice4, notice5));

        // when
        List<DepartmentNotice> results = noticeUpdateSupport
                .filteringSoonSaveDepartmentNotices(originNotices, List.of(1, 2, 4), DepartmentName.ECONOMICS, false);

        // then
        assertThat(results).hasSize(2)
                .extracting("articleId")
                .containsExactly("3", "5");
    }

    @DisplayName("삭제되어야 할 공지의 article id를 걸러낸다")
    @Test
    public void filteringSoonDeleteNoticeIds() {
        // given
        List<String> savedArticleIds = List.of("5b54bcd", "5b54bce", "5b54bcf", "5b54bcg");
        List<String> latestNoticeIds = List.of("5b54bcd", "5b54bce", "5b54bcg");

        // when
        List<String> results = noticeUpdateSupport.filteringSoonDeleteNoticeIds(savedArticleIds, latestNoticeIds);

        // then
        assertThat(results).hasSize(1)
                .containsExactly("5b54bcf");
    }

    @DisplayName("삭제되어야 할 학과별 공지의 article id를 걸러낸다")
    @Test
    public void filteringSoonDeleteDepartmentNoticeIds() {
        // given
        List<Integer> savedArticleIds = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        List<Integer> latestNoticeIds = List.of(1, 4, 5, 6, 8, 10);

        // when
        List<String> results = noticeUpdateSupport.filteringSoonDeleteDepartmentNoticeIds(savedArticleIds, latestNoticeIds);

        // then
        assertThat(results).hasSize(4)
                .containsExactly("2", "3", "7", "9");
    }

    @DisplayName("새롭게 scrap된 공지들 결과물로부터 id를 추출한다")
    @Test
    public void extractNoticeIds() {
        // given
        List<CommonNoticeFormatDto> originNotices = new ArrayList<>();

        CommonNoticeFormatDto notice1 = CommonNoticeFormatDto.builder().articleId("5b54bcd").updatedDate("2021-01-01").subject("library1")
                .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71921").important(false).build();
        CommonNoticeFormatDto notice2 = CommonNoticeFormatDto.builder().articleId("5b54bce").updatedDate("2021-01-01").subject("library1")
                .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71921").important(false).build();
        CommonNoticeFormatDto notice3 = CommonNoticeFormatDto.builder().articleId("5b54bcf").updatedDate("2021-01-01").subject("library1")
                .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71921").important(false).build();
        CommonNoticeFormatDto notice4 = CommonNoticeFormatDto.builder().articleId("5b54bcg").updatedDate("2021-01-01").subject("library1")
                .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71921").important(false).build();
        CommonNoticeFormatDto notice5 = CommonNoticeFormatDto.builder().articleId("5b54bch").updatedDate("2021-01-01").subject("library1")
                .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71921").important(false).build();

        originNotices.addAll(List.of(notice1, notice2, notice3, notice4, notice5));

        // when
        List<String> results = noticeUpdateSupport.extractNoticeIds(originNotices);

        // then
        assertThat(results).hasSize(5)
                .containsExactly("5b54bcd", "5b54bce", "5b54bcf", "5b54bcg", "5b54bch");
    }

    @DisplayName("새롭게 scrap된 학과별 공지들 결과물로부터 id를 추출한다")
    @Test
    public void extractDepartmentNoticeIds() {
        // given
        List<CommonNoticeFormatDto> originNotices = new ArrayList<>();

        CommonNoticeFormatDto notice1 = CommonNoticeFormatDto.builder().articleId("1").updatedDate("2021-01-01").subject("library1")
                .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71921").important(false).build();
        CommonNoticeFormatDto notice2 = CommonNoticeFormatDto.builder().articleId("2").updatedDate("2021-01-01").subject("library1")
                .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71921").important(false).build();
        CommonNoticeFormatDto notice3 = CommonNoticeFormatDto.builder().articleId("3").updatedDate("2021-01-01").subject("library1")
                .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71921").important(false).build();
        CommonNoticeFormatDto notice4 = CommonNoticeFormatDto.builder().articleId("4").updatedDate("2021-01-01").subject("library1")
                .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71921").important(false).build();
        CommonNoticeFormatDto notice5 = CommonNoticeFormatDto.builder().articleId("5").updatedDate("2021-01-01").subject("library1")
                .postedDate("2021-01-01").fullUrl("https://library.konkuk.ac.kr/library-guide/bulletins/notice/71921").important(false).build();

        originNotices.addAll(List.of(notice1, notice2, notice3, notice4, notice5));

        // when
        List<Integer> results = noticeUpdateSupport.extractDepartmentNoticeIds(originNotices);

        // then
        assertThat(results).hasSize(5)
                .containsExactly(1, 2, 3, 4, 5);
    }
}
