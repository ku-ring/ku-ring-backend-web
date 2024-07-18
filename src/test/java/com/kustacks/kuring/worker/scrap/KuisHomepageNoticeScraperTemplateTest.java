package com.kustacks.kuring.worker.scrap;

import com.kustacks.kuring.notice.application.port.out.dto.NoticeDto;
import com.kustacks.kuring.support.IntegrationTestSupport;
import com.kustacks.kuring.support.TestFileLoader;
import com.kustacks.kuring.worker.dto.ComplexNoticeFormatDto;
import com.kustacks.kuring.worker.parser.notice.PageTextDto;
import com.kustacks.kuring.worker.scrap.client.NormalJsoupClient;
import com.kustacks.kuring.worker.scrap.noticeinfo.BachelorKuisHomepageNoticeInfo;
import com.kustacks.kuring.worker.scrap.noticeinfo.KuisHomepageNoticeInfo;
import com.kustacks.kuring.worker.scrap.noticeinfo.StudentKuisHomepageNoticeInfo;
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

class KuisHomepageNoticeScraperTemplateTest extends IntegrationTestSupport {

    @MockBean
    private NormalJsoupClient normalJsoupClient;

    @Autowired
    private KuisHomepageNoticeScraperTemplate scraperTemplate;

    @Autowired
    private StudentKuisHomepageNoticeInfo studentKuisHomepageNoticeInfo;

    @Autowired
    private BachelorKuisHomepageNoticeInfo bachelorKuisHomepageNoticeInfo;

    @DisplayName("Kuis 공지의 최신 페이지를 스크래핑한다")
    @Test
    void request() throws IOException {
        // given
        Document doc = Jsoup.parse(
                TestFileLoader.loadHtmlFile("src/test/resources/notice/student-notice-2024.html")
        );
        when(normalJsoupClient.get(anyString(), anyInt())).thenReturn(doc);

        // when
        List<ComplexNoticeFormatDto> results = scraperTemplate.scrap(
                studentKuisHomepageNoticeInfo, KuisHomepageNoticeInfo::scrapLatestPageHtml);

        // then
        assertAll(
                () -> assertThat(results).hasSize(1),
                () -> assertThat(results.get(0).getImportantNoticeList()).hasSize(22),
                () -> assertThat(results.get(0).getNormalNoticeList()).hasSize(2)
        );
    }

    @DisplayName("Kuis 공지의 최신 페이지를 embedding 한다")
    @Test
    void scrapForEmbedding() throws IOException {
        // given
        Document doc = Jsoup.parse(
                TestFileLoader.loadHtmlFile("src/test/resources/notice/bbs-article-2024.html")
        );

        NoticeDto noticeDto = new NoticeDto("1", "2024-01-01 00:00:00",
                "http://example.com", "제목", "category", true);

        when(normalJsoupClient.get(anyString(), anyInt())).thenReturn(doc);

        // when
        List<PageTextDto> pageTextDtos = scraperTemplate.scrapForEmbedding(List.of(noticeDto), bachelorKuisHomepageNoticeInfo);

        // then
        assertAll(
                () -> assertThat(pageTextDtos).hasSize(1),
                () -> assertThat(pageTextDtos.get(0).articleId()).isEqualTo("1117110"),
                () -> assertThat(pageTextDtos.get(0).title()).isEqualTo("2024년도 대학생 청소년교육지원 장학사업 멘토모집 안내")
        );
    }
}
