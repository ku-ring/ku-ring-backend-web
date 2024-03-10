package com.kustacks.kuring.worker.scrap.client.notice;

import com.kustacks.kuring.worker.scrap.client.JsoupClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LatestPageNoticeApiClientTest {

    @Mock
    private JsoupClient jsoupClient;

    @DisplayName("공지의 총 개수를 가져온다.")
    @Test
    void getTotalNoticeSize() throws IOException {
        // given
        Document doc = Jsoup.parse(loadHtmlFile("src/test/resources/notice/cse-notice-2024.html"));
        when(jsoupClient.get(anyString(), anyInt())).thenReturn(doc);
        String url = "https://cse.konkuk.ac.kr/cse/9962/subview.do";

        // when
        int totalNoticeSize = new LatestPageNoticeApiClient(jsoupClient).getTotalNoticeSize(url);

        // then
        assertThat(totalNoticeSize).isEqualTo(625);
    }

    private static String loadHtmlFile(String filePath) throws IOException {
        Path path = Path.of(filePath);
        byte[] fileBytes = Files.readAllBytes(path);
        return new String(fileBytes, StandardCharsets.UTF_8);
    }
}
