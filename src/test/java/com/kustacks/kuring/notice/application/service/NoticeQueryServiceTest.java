package com.kustacks.kuring.notice.application.service;

import com.kustacks.kuring.notice.application.port.in.dto.NoticeWeeklyTopResult;
import com.kustacks.kuring.notice.application.port.out.CommentQueryPort;
import com.kustacks.kuring.notice.application.port.out.NoticeQueryPort;
import com.kustacks.kuring.notice.application.port.out.NoticeRankingQueryPort;
import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.notice.domain.NoticeWeeklyRanking;
import com.kustacks.kuring.user.application.port.out.RootUserQueryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.when;

@DisplayName("서비스 : NoticeQueryService")
@ExtendWith(MockitoExtension.class)
class NoticeQueryServiceTest {

    @Mock
    private NoticeQueryPort noticeQueryPort;

    @Mock
    private NoticeRankingQueryPort noticeRankingQueryPort;

    @Mock
    private CommentQueryPort commentQueryPort;

    @Mock
    private RootUserQueryPort rootUserQueryPort;

    private NoticeQueryService noticeQueryService;

    @BeforeEach
    void setUp() {
        noticeQueryService = new NoticeQueryService(
                noticeQueryPort,
                noticeRankingQueryPort,
                commentQueryPort,
                rootUserQueryPort,
                List.of()
        );
    }

    @DisplayName("가장 최근에 집계된 주간 인기 공지를 순위대로 조회한다")
    @Test
    void getWeeklyTopNotices() {
        // given
        LocalDate weekStartDate = LocalDate.of(2026, 10, 19);
        when(noticeRankingQueryPort.findLatestWeeklyRankings()).thenReturn(List.of(
                NoticeWeeklyRanking.builder()
                        .weekStartDate(weekStartDate).ranking(1).noticeId(1L).articleId("1210682")
                        .subject("수강신청 안내").categoryName(CategoryName.BACHELOR)
                        .postedDate(LocalDateTime.of(2026, 10, 20, 9, 30))
                        .url("https://www.konkuk.ac.kr/bbs/konkuk/234/1210682/artclView.do").viewCount(1500)
                        .build(),
                NoticeWeeklyRanking.builder()
                        .weekStartDate(weekStartDate).ranking(2).noticeId(2L).articleId("1132173")
                        .subject("졸업요건 안내").categoryName(CategoryName.SCHOLARSHIP)
                        .postedDate(LocalDateTime.of(2024, 7, 25, 8, 0))
                        .url("https://www.konkuk.ac.kr/bbs/konkuk/231/1132173/artclView.do").viewCount(800)
                        .build()
        ));

        // when
        List<NoticeWeeklyTopResult> results = noticeQueryService.getWeeklyTopNotices();

        // then
        assertThat(results)
                .extracting("id", "subject", "viewCount", "category", "categoryKorName", "postedDate")
                .containsExactly(
                        tuple(1L, "수강신청 안내", 1500, "bachelor", "학사", "2026-10-20"),
                        tuple(2L, "졸업요건 안내", 800, "scholarship", "장학", "2024-07-25")
                );
    }
}
