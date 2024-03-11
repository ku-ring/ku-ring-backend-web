package com.kustacks.kuring.worker.scrap.client.notice;

import com.kustacks.kuring.support.IntegrationTestSupport;
import com.kustacks.kuring.support.TestFileLoader;
import com.kustacks.kuring.worker.dto.ScrapingResultDto;
import com.kustacks.kuring.worker.scrap.client.JsoupClient;
import com.kustacks.kuring.worker.scrap.noticeinfo.StudentKuisHomepageNoticeInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@DisplayName("클라이언트 : KUIS 홈페이지 공지")
class KuisHomepageNoticeApiClientTest extends IntegrationTestSupport {

    @Mock
    private JsoupClient jsoupClient;

    @Autowired
    private StudentKuisHomepageNoticeInfo studentKuisHomepageNoticeInfo;

    @DisplayName("공지의 최신 페이지를 가져온다")
    @Test
    void request() throws IOException {
        // given
        Document doc = Jsoup.parse(
                TestFileLoader.loadHtmlFile("src/test/resources/notice/student-notice-2024.html")
        );
        when(jsoupClient.get(anyString(), anyInt())).thenReturn(doc);

        // when
        List<ScrapingResultDto> results = new KuisHomepageNoticeApiClient(jsoupClient)
                .request(studentKuisHomepageNoticeInfo);

        // then
        assertAll(
                () -> assertThat(results).hasSize(1),
                () -> assertThat(results.get(0).getDocument()).isNotNull(),
                () -> assertThat(results.get(0).getViewUrl())
                        .isEqualTo("https://www.konkuk.ac.kr/bbs/konkuk/238/{noticeId}/artclView.do")
        );
    }

    @DisplayName("공지의 총 개수를 가져온다.")
    @Test
    void getTotalNoticeSize() throws IOException {
        // given
        Document doc = Jsoup.parse(
                TestFileLoader.loadHtmlFile("src/test/resources/notice/student-notice-2024.html")
        );
        when(jsoupClient.get(anyString(), anyInt())).thenReturn(doc);
        String url = "https://www.konkuk.ac.kr/konkuk/238/subview.do";

        // when
        int totalNoticeSize = new KuisHomepageNoticeApiClient(jsoupClient).getTotalNoticeSize(url);

        // then
        assertThat(totalNoticeSize).isEqualTo(396);
    }
}
