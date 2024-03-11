package com.kustacks.kuring.worker.scrap;

import com.kustacks.kuring.support.IntegrationTestSupport;
import com.kustacks.kuring.support.TestFileLoader;
import com.kustacks.kuring.worker.dto.ComplexNoticeFormatDto;
import com.kustacks.kuring.worker.scrap.client.NormalJsoupClient;
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
    private KuisHomepageNoticeScraperTemplate scraperTemplateTest;

    @Autowired
    private StudentKuisHomepageNoticeInfo studentKuisHomepageNoticeInfo;

    @DisplayName("Kuis 공지의 최신 페이지를 스크래핑한다.")
    @Test
    void request() throws IOException {
        // given
        Document doc = Jsoup.parse(
                TestFileLoader.loadHtmlFile("src/test/resources/notice/student-notice-2024.html")
        );
        when(normalJsoupClient.get(anyString(), anyInt())).thenReturn(doc);

        // when
        List<ComplexNoticeFormatDto> results = scraperTemplateTest.scrap(
                studentKuisHomepageNoticeInfo, KuisHomepageNoticeInfo::scrapLatestPageHtml);

        // then
        assertAll(
                () -> assertThat(results).hasSize(1),
                () -> assertThat(results.get(0).getImportantNoticeList()).hasSize(22),
                () -> assertThat(results.get(0).getNormalNoticeList()).hasSize(10)
        );
    }
}
