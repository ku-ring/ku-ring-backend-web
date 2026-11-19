package com.kustacks.kuring.notice.application.service;

import com.kustacks.kuring.email.adapter.out.email.ThymeleafTemplateEngineAdapter;
import com.kustacks.kuring.notice.application.port.in.dto.NoticeWeeklyTopResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("템플릿 : 주간 인기 공지 리포트 메일")
class NoticeWeeklyRankingReportTemplateTest {

    @DisplayName("기간과 순위별 공지 정보를 메일 본문 표로 그린다")
    @Test
    void renderWeeklyRankingReport() {
        // given
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(resolver);

        List<NoticeWeeklyTopResult> rankings = List.of(
                new NoticeWeeklyTopResult(1L, "1210289", "2026학년도 동계 계절학기 예비수강신청 안내", "bachelor", "학사",
                        1968, "2026-10-20", "https://www.konkuk.ac.kr/bbs/konkuk/234/1210289/artclView.do")
        );

        // when
        String html = new ThymeleafTemplateEngineAdapter(templateEngine).process("mail/notice-weekly-ranking", Map.of(
                "weekStartDate", "2026-10-26",
                "weekEndDate", "2026-11-01",
                "rankings", rankings
        ));

        // then
        assertThat(html)
                .contains("2026-10-26 ~ 2026-11-01")
                .contains("학사")
                .contains("2026학년도 동계 계절학기 예비수강신청 안내")
                .contains("href=\"https://www.konkuk.ac.kr/bbs/konkuk/234/1210289/artclView.do\"")
                .contains("2026-10-20")
                .contains("1,968");
    }
}
