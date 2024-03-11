package com.kustacks.kuring.worker.scrap.client.notice;

import com.kustacks.kuring.support.IntegrationTestSupport;
import com.kustacks.kuring.support.TestFileLoader;
import com.kustacks.kuring.worker.dto.ScrapingResultDto;
import com.kustacks.kuring.worker.scrap.client.NormalJsoupClient;
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
    private NormalJsoupClient jsoupClient;

    @Autowired
    private StudentKuisHomepageNoticeInfo studentKuisHomepageNoticeInfo;

    @DisplayName("Kuis 공지의 최신 페이지를 가져온다")
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

    @DisplayName("Kuis 공지 전체 페이지를 스크래핑한다.")
    @Test
    void requestAll() throws IOException {
        // given
        Document doc = Jsoup.parse(
                TestFileLoader.loadHtmlFile("src/test/resources/notice/student-notice-2024.html")
        );
        when(jsoupClient.get(anyString(), anyInt())).thenReturn(doc);

        // when
        List<ScrapingResultDto> results = new KuisHomepageNoticeApiClient(jsoupClient)
                .requestAll(studentKuisHomepageNoticeInfo);

        // then
        assertAll(
                () -> assertThat(results).hasSize(1),
                () -> assertThat(results.get(0).getDocument()).isNotNull(),
                () -> assertThat(results.get(0).getViewUrl())
                        .isEqualTo("https://www.konkuk.ac.kr/bbs/konkuk/238/{noticeId}/artclView.do")
        );
    }

    @DisplayName("Kuis 공지 전체 페이지중 예외가 발생하면 빈 리스트를 반환한다.")
    @Test
    void requestAll_exception() throws IOException {
        // given
        KuisHomepageNoticeApiClient noticeApiClient = new KuisHomepageNoticeApiClient(jsoupClient);

        when(jsoupClient.get(anyString(), anyInt())).thenThrow(new IOException("test"));

        // when
        List<ScrapingResultDto> results = noticeApiClient.requestAll(studentKuisHomepageNoticeInfo);

        // then
        assertThat(results).isEmpty();
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

    @DisplayName("공지의 총 개수가 없는 경우 한 페이지의 최대값인 650으로 가정한다.")
    @Test
    void getTotalNoticeSize_side() throws IOException {
        // given
        Document doc = Jsoup.parse(
                TestFileLoader.loadHtmlFile("src/test/resources/notice/student-notice-no-total-count-2024.html")
        );
        when(jsoupClient.get(anyString(), anyInt())).thenReturn(doc);
        String url = "https://www.konkuk.ac.kr/konkuk/238/subview.do";

        // when
        int totalNoticeSize = new KuisHomepageNoticeApiClient(jsoupClient).getTotalNoticeSize(url);

        // then
        assertThat(totalNoticeSize).isEqualTo(650);
    }
}
