package com.kustacks.kuring.notice.application.service;

import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.email.application.port.out.EmailClientPort;
import com.kustacks.kuring.email.application.port.out.TemplateEnginePort;
import com.kustacks.kuring.notice.application.port.in.NoticeWeeklyRankingReportUseCase;
import com.kustacks.kuring.notice.application.port.in.dto.NoticeWeeklyTopResult;
import com.kustacks.kuring.notice.application.port.out.NoticeRankingExcelPort;
import com.kustacks.kuring.notice.application.port.out.NoticeRankingQueryPort;
import com.kustacks.kuring.notice.domain.NoticeWeeklyRanking;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class NoticeWeeklyRankingReportService implements NoticeWeeklyRankingReportUseCase {

    private static final String TEMPLATE_FILE = "mail/notice-weekly-ranking";
    private static final String FROM_EMAIL = "no-reply@ku-ring.com";
    private static final String SUBJECT_FORMAT = "[쿠링] 주간 인기 공지 (%s ~ %s)";
    private static final String ATTACHMENT_NAME_FORMAT = "notice-weekly-ranking-%s.xlsx";
    private static final String EXCEL_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private final NoticeRankingQueryPort noticeRankingQueryPort;
    private final NoticeRankingExcelPort noticeRankingExcelPort;
    private final TemplateEnginePort templateEnginePort;
    private final EmailClientPort emailClientPort;
    private final NoticeWeeklyRankingReportProperties reportProperties;
    private final Clock clock;

    @Override
    public void sendWeeklyRankingReport() {
        if (!reportProperties.hasRecipients()) {
            log.info("주간 인기 공지 리포트 수신자가 없어 메일을 보내지 않습니다");
            return;
        }

        LocalDate weekStartDate = LocalDate.now(clock).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        List<NoticeWeeklyRanking> rankings = noticeRankingQueryPort.findLatestWeeklyRankings();

        if (rankings.isEmpty() || !rankings.get(0).getWeekStartDate().equals(weekStartDate)) {
            log.warn("이번 주({}) 인기 공지 순위가 없어 리포트 메일을 보내지 않습니다", weekStartDate);
            return;
        }

        LocalDate weekEndDate = weekStartDate.plusDays(6);
        String html = templateEnginePort.process(TEMPLATE_FILE, Map.of(
                "weekStartDate", weekStartDate.toString(),
                "weekEndDate", weekEndDate.toString(),
                "rankings", rankings.stream().map(NoticeWeeklyTopResult::from).toList()
        ));
        byte[] excel = noticeRankingExcelPort.createWeeklyRankingExcel(weekStartDate, rankings);

        emailClientPort.sendEmailAsync(createMimeMessage(
                SUBJECT_FORMAT.formatted(weekStartDate, weekEndDate),
                html,
                ATTACHMENT_NAME_FORMAT.formatted(weekStartDate),
                excel
        ));
        log.info("주간 인기 공지 리포트 메일 발송을 요청했습니다. 수신자 {}명", reportProperties.recipients().size());
    }

    private MimeMessage createMimeMessage(String subject, String html, String attachmentName, byte[] attachment) {
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(new MimeMessage(Session.getInstance(System.getProperties())), true, "utf-8");
            mimeMessageHelper.setFrom(FROM_EMAIL);
            mimeMessageHelper.setTo(reportProperties.recipients().toArray(String[]::new));
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(html, true);
            mimeMessageHelper.addAttachment(attachmentName, new ByteArrayResource(attachment), EXCEL_CONTENT_TYPE);
            return mimeMessageHelper.getMimeMessage();
        } catch (MessagingException e) {
            throw new InternalLogicException(ErrorCode.NOTICE_RANKING_REPORT_CANNOT_CREATE_MAIL, e);
        }
    }
}
