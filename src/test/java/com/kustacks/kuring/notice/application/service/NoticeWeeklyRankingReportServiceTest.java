package com.kustacks.kuring.notice.application.service;

import com.kustacks.kuring.email.application.port.out.EmailClientPort;
import com.kustacks.kuring.email.application.port.out.TemplateEnginePort;
import com.kustacks.kuring.notice.application.port.out.NoticeRankingExcelPort;
import com.kustacks.kuring.notice.application.port.out.NoticeRankingQueryPort;
import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.notice.domain.NoticeWeeklyRanking;
import jakarta.mail.Address;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@DisplayName("서비스 : NoticeWeeklyRankingReportService")
@ExtendWith(MockitoExtension.class)
class NoticeWeeklyRankingReportServiceTest {

    private static final Clock SUNDAY_NIGHT_CLOCK = Clock.fixed(
            Instant.parse("2026-11-01T14:30:00Z"),
            ZoneId.of("Asia/Seoul")
    );
    private static final LocalDate WEEK_START_DATE = LocalDate.of(2026, 10, 26);
    private static final List<String> RECIPIENTS = List.of("a@ku-ring.com", "b@ku-ring.com");

    @Mock
    private NoticeRankingQueryPort noticeRankingQueryPort;

    @Mock
    private NoticeRankingExcelPort noticeRankingExcelPort;

    @Mock
    private TemplateEnginePort templateEnginePort;

    @Mock
    private EmailClientPort emailClientPort;

    @Captor
    private ArgumentCaptor<MimeMessage> mimeMessageCaptor;

    @Captor
    private ArgumentCaptor<Map<String, Object>> variablesCaptor;

    @DisplayName("이번 주 순위를 메일 본문과 엑셀 첨부로 만들어 모든 수신자에게 보낸다")
    @Test
    void sendWeeklyRankingReport() throws Exception {
        // given
        List<NoticeWeeklyRanking> rankings = List.of(ranking(WEEK_START_DATE, 1), ranking(WEEK_START_DATE, 2));
        when(noticeRankingQueryPort.findLatestWeeklyRankings()).thenReturn(rankings);
        when(templateEnginePort.process(eq("mail/notice-weekly-ranking"), variablesCaptor.capture())).thenReturn("<html>report</html>");
        when(noticeRankingExcelPort.createWeeklyRankingExcel(WEEK_START_DATE, rankings)).thenReturn(new byte[]{1, 2, 3});

        // when
        createService(RECIPIENTS).sendWeeklyRankingReport();

        // then
        verify(emailClientPort).sendEmailAsync(mimeMessageCaptor.capture());
        MimeMessage message = mimeMessageCaptor.getValue();

        assertAll(
                () -> assertThat(Arrays.stream(message.getRecipients(Message.RecipientType.TO)).map(Address::toString))
                        .containsExactly("a@ku-ring.com", "b@ku-ring.com"),
                () -> assertThat(message.getSubject()).isEqualTo("[쿠링] 주간 인기 공지 (2026-10-26 ~ 2026-11-01)"),
                () -> assertThat(attachmentFileNames(message)).containsExactly("notice-weekly-ranking-2026-10-26.xlsx"),
                () -> assertThat(variablesCaptor.getValue())
                        .containsEntry("weekStartDate", "2026-10-26")
                        .containsEntry("weekEndDate", "2026-11-01"),
                () -> assertThat((List<?>) variablesCaptor.getValue().get("rankings")).hasSize(2)
        );
    }

    @DisplayName("수신자가 지정되지 않았으면 메일을 보내지 않는다")
    @Test
    void doNotSendWithoutRecipients() {
        // when
        createService(List.of()).sendWeeklyRankingReport();

        // then
        verifyNoInteractions(noticeRankingQueryPort, emailClientPort);
    }

    @DisplayName("이번 주 집계가 없어 지난주 순위만 남아 있으면 메일을 보내지 않는다")
    @Test
    void doNotSendLastWeekRanking() {
        // given
        when(noticeRankingQueryPort.findLatestWeeklyRankings())
                .thenReturn(List.of(ranking(WEEK_START_DATE.minusWeeks(1), 1)));

        // when
        createService(RECIPIENTS).sendWeeklyRankingReport();

        // then
        verifyNoInteractions(emailClientPort, noticeRankingExcelPort);
    }

    @DisplayName("집계된 순위가 하나도 없으면 메일을 보내지 않는다")
    @Test
    void doNotSendWithoutRanking() {
        // given
        when(noticeRankingQueryPort.findLatestWeeklyRankings()).thenReturn(List.of());

        // when
        createService(RECIPIENTS).sendWeeklyRankingReport();

        // then
        verifyNoInteractions(emailClientPort, noticeRankingExcelPort);
    }

    private NoticeWeeklyRankingReportService createService(List<String> recipients) {
        return new NoticeWeeklyRankingReportService(
                noticeRankingQueryPort,
                noticeRankingExcelPort,
                templateEnginePort,
                emailClientPort,
                new NoticeWeeklyRankingReportProperties(recipients),
                SUNDAY_NIGHT_CLOCK
        );
    }

    private static List<String> attachmentFileNames(MimeMessage message) throws Exception {
        MimeMultipart multipart = (MimeMultipart) message.getContent();
        List<String> fileNames = new ArrayList<>();
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart part = multipart.getBodyPart(i);
            if (part.getFileName() != null) {
                fileNames.add(part.getFileName());
            }
        }
        return fileNames;
    }

    private static NoticeWeeklyRanking ranking(LocalDate weekStartDate, int ranking) {
        return NoticeWeeklyRanking.builder()
                .weekStartDate(weekStartDate)
                .ranking(ranking)
                .noticeId((long) ranking)
                .articleId(String.valueOf(ranking))
                .subject("subject_" + ranking)
                .categoryName(CategoryName.BACHELOR)
                .postedDate(LocalDateTime.of(2026, 10, 27, 9, 0))
                .url("https://www.example.com/" + ranking)
                .viewCount(100 * ranking)
                .build();
    }
}
