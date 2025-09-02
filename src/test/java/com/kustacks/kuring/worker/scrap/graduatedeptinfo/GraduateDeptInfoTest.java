package com.kustacks.kuring.worker.scrap.graduatedeptinfo;

import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.support.IntegrationTestSupport;
import com.kustacks.kuring.worker.dto.ComplexNoticeFormatDto;
import com.kustacks.kuring.worker.scrap.DepartmentNoticeScraperTemplate;
import com.kustacks.kuring.worker.scrap.client.NormalJsoupClient;
import com.kustacks.kuring.worker.scrap.client.notice.LatestPageNoticeApiClient;

import com.kustacks.kuring.worker.update.notice.dto.response.CommonNoticeFormatDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class GraduateDeptInfoTest extends IntegrationTestSupport {
    @MockBean
    private NormalJsoupClient normalJsoupClient;

    @Autowired
    private DepartmentNoticeScraperTemplate scraperTemplate;

    @Autowired
    private ComputerScienceGraduateDept graduateDept;

    @Autowired
    private LatestPageNoticeApiClient latestPageNoticeApiClient;

    @Test
    @DisplayName("컴퓨터공학부 대학원 공지를 스크래핑한다.")
    void computer_scrapLatestPage() throws IOException {
        // given
        Document doc = Jsoup.parse(
                getClass().getClassLoader().getResourceAsStream("notice/graduate-cse-notice.html"),
                "UTF-8",
                ""
        );

        when(normalJsoupClient.get(anyString(), anyInt())).thenReturn(doc);

        // when
        List<ComplexNoticeFormatDto> results =
                scraperTemplate.scrap(graduateDept, latestPageNoticeApiClient::request);

        // then
        assertThat(results).isNotEmpty();
        assertThat(graduateDept.getDepartmentName()).isEqualTo(DepartmentName.COMPUTER_GRADUATE);

        ComplexNoticeFormatDto dto = results.get(0);

        List<CommonNoticeFormatDto> importantNotices = dto.getImportantNoticeList();
        List<CommonNoticeFormatDto> normalNotices = dto.getNormalNoticeList();


        assertAll(
                // important 검증
                () -> assertThat(importantNotices).hasSize(1),
                () -> assertThat(importantNotices.get(0).getArticleId()).isEqualTo("1139134"),
                () -> assertThat(importantNotices.get(0).getSubject())
                        .isEqualTo("[내규] 일반대학원 컴퓨터공학과 내규"),
                () -> assertThat(importantNotices.get(0).getPostedDate()).isEqualTo("2024.11.14"),
                () -> assertThat(importantNotices.get(0).getFullUrl())
                        .isEqualTo("https://cse.konkuk.ac.kr/bbs/cse/411/1139134/artclView.do"),
                () -> assertThat(importantNotices.get(0).getImportant()).isTrue(),

                // normal 검증
                () -> assertThat(normalNotices).hasSize(10),
                () -> assertThat(normalNotices.get(0).getArticleId()).isEqualTo("1154434"),
                () -> assertThat(normalNotices.get(0).getSubject())
                        .isEqualTo("2025.2학기 대학원 연구등록금 납부일정 안내"),
                () -> assertThat(normalNotices.get(0).getPostedDate()).isEqualTo("2025.07.25"),
                () -> assertThat(normalNotices.get(0).getFullUrl())
                        .isEqualTo("https://cse.konkuk.ac.kr/bbs/cse/411/1154434/artclView.do"),
                () -> assertThat(normalNotices.get(0).getImportant()).isFalse()
        );
    }
}
