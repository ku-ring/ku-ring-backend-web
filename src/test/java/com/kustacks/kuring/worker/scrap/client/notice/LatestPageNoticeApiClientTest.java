package com.kustacks.kuring.worker.scrap.client.notice;

import com.kustacks.kuring.support.TestFileLoader;
import com.kustacks.kuring.worker.scrap.client.NormalJsoupClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.dto.ScrapingResultDto;
import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.common.exception.code.ErrorCode;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class LatestPageNoticeApiClientTest {

    @Mock
    private NormalJsoupClient jsoupClient;

    @DisplayName("tr merge 및 총 tr 갯수 검증 테스트")
    @Test
    void requestAll_merge_trs_and_match_total_count() throws Exception {
        // given
        LatestPageNoticeApiClient apiClient = new LatestPageNoticeApiClient(jsoupClient);

        DeptInfo deptInfo = mock(DeptInfo.class);
        when(deptInfo.createUndergraduateViewUrl()).thenReturn("https://example.com/view");

        when(deptInfo.createUndergraduateRequestUrl(anyInt(), anyInt()))
                .thenAnswer(arg -> {
                    int page = arg.getArgument(0);
                    int row = arg.getArgument(1);
                    return "https://example.com/list?page=" + page + "&row=" + row;
                });

        int total = 230; // 100+100+30
        String totalUrl = "https://example.com/list?page=1&row=1";
        String page1 = "https://example.com/list?page=1&row=100";
        String page2 = "https://example.com/list?page=2&row=100";
        String page3 = "https://example.com/list?page=3&row=100";

        when(jsoupClient.get(anyString(), anyInt()))
                .thenAnswer(arg -> {
                    String url = arg.getArgument(0);

                    if (url.equals(totalUrl))
                        return docWithTotalCount(total);
                    if (url.equals(page1))
                        return docWithTableRows(100);
                    if (url.equals(page2))
                        return docWithTableRows(100);
                    if (url.equals(page3))
                        return docWithTableRows(30);

                    throw new IllegalStateException("Unexpected url: " + url);
                });

        // when
        List<ScrapingResultDto> result = apiClient.requestAll(deptInfo);

        // then
        assertThat(result).hasSize(1);

        Document merged = result.get(0).getDocument();
        assertThat(merged.select(".board-table > tbody > tr").size()).isEqualTo(total);

        // 호출을 모두 했는지 검증
        verify(jsoupClient).get(eq(totalUrl), anyInt());
        verify(jsoupClient).get(eq(page1), anyInt());
        verify(jsoupClient).get(eq(page2), anyInt());
        verify(jsoupClient).get(eq(page3), anyInt());
    }

    @DisplayName("base 페이지에 tbody가 없으면 NOTICE_SCRAPER_CANNOT_SCRAP 에러를 발생시킨다.")
    @Test
    void requestAll_throw_notice_scrap_exception_when_no_tbody() throws Exception {
        // given
        LatestPageNoticeApiClient apiClient = new LatestPageNoticeApiClient(jsoupClient);

        DeptInfo deptInfo = mock(DeptInfo.class);
        when(deptInfo.getDeptName()).thenReturn("COMPUTER");

        when(deptInfo.createUndergraduateRequestUrl(anyInt(), anyInt()))
                .thenAnswer(arg -> "https://example.com/list?page=" + arg.getArgument(0) + "&row=" + arg.getArgument(1));

        String totalUrl = "https://example.com/list?page=1&row=1";
        String firstUrl = "https://example.com/list?page=1&row=100";

        when(jsoupClient.get(eq(totalUrl), anyInt()))
                .thenReturn(docWithTotalCount(10));

        // 총 공지 갯수가 10이였으나 baseDoc에 tbody 없는 경우
        when(jsoupClient.get(eq(firstUrl), anyInt()))
                .thenReturn(Jsoup.parse("<html><head><title>x</title></head><body><div>no table</div></body></html>"));

        // when & then
        assertThatThrownBy(() -> apiClient.requestAll(deptInfo))
                .isInstanceOf(InternalLogicException.class);
    }

    @DisplayName("IOException이 발생하면 empty 반환한다")
    @Test
    void requestAll_return_empty_when_io_exception_occurs() throws Exception {
        // given
        LatestPageNoticeApiClient apiClient = new LatestPageNoticeApiClient(jsoupClient);

        DeptInfo deptInfo = mock(DeptInfo.class);
        when(deptInfo.createUndergraduateRequestUrl(anyInt(), anyInt()))
                .thenAnswer(arg -> "https://example.com/list?page=" + arg.getArgument(0) + "&row=" + arg.getArgument(1));

        String totalUrl = "https://example.com/list?page=1&row=1";

        // total count 단계에서 IOException 발생
        when(jsoupClient.get(eq(totalUrl), anyInt())).thenThrow(new IOException("network error"));

        // when
        List<ScrapingResultDto> result = apiClient.requestAll(deptInfo);

        // then
        assertThat(result).isEmpty();
    }

    @DisplayName("중간 페이지에서 tr이 비면 break 한다(이후 페이지 호출 안 함)")
    @Test
    void requestAll_break_when_page_has_no_trs() throws Exception {
        LatestPageNoticeApiClient apiClient = new LatestPageNoticeApiClient(jsoupClient);

        DeptInfo deptInfo = mock(DeptInfo.class);
        when(deptInfo.createUndergraduateViewUrl()).thenReturn("https://example.com/view");
        when(deptInfo.createUndergraduateRequestUrl(anyInt(), anyInt()))
                .thenAnswer(arg -> "https://example.com/list?page=" + arg.getArgument(0) + "&row=" + arg.getArgument(1));

        String totalUrl = "https://example.com/list?page=1&row=1";
        String page1 = "https://example.com/list?page=1&row=100";
        String page2 = "https://example.com/list?page=2&row=100";
        String page3 = "https://example.com/list?page=3&row=100";

        when(jsoupClient.get(eq(totalUrl), anyInt())).thenReturn(docWithTotalCount(250));
        when(jsoupClient.get(eq(page1), anyInt())).thenReturn(docWithTableRows(100));
        when(jsoupClient.get(eq(page2), anyInt())).thenReturn(docWithTableRows(0)); // 여기서 break 유도

        List<ScrapingResultDto> result = apiClient.requestAll(deptInfo);

        assertThat(result).hasSize(1);
        Document merged = result.get(0).getDocument();
        assertThat(merged.select(".board-table > tbody > tr").size()).isEqualTo(100);

        verify(jsoupClient, times(1)).get(eq(totalUrl), anyInt());
        verify(jsoupClient, times(1)).get(eq(page1), anyInt());
        verify(jsoupClient, times(1)).get(eq(page2), anyInt());
        verify(jsoupClient, never()).get(eq(page3), anyInt());
    }

    @DisplayName("최신 1페이지 요청 시 dto 1개를 반환한다")
    @Test
    void request_return_single_dto() throws Exception {
        // given
        LatestPageNoticeApiClient apiClient = new LatestPageNoticeApiClient(jsoupClient);

        DeptInfo deptInfo = mock(DeptInfo.class);
        when(deptInfo.createUndergraduateViewUrl()).thenReturn("https://example.com/view");
        when(deptInfo.createUndergraduateRequestUrl(eq(1), anyInt()))
                .thenReturn("https://example.com/list?page=1&row=20");

        when(jsoupClient.get(eq("https://example.com/list?page=1&row=20"), anyInt()))
                .thenReturn(docWithTableRows(5));

        // when
        List<ScrapingResultDto> result = apiClient.request(deptInfo);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDocument()).isNotNull();

        verify(jsoupClient, times(1)).get(eq("https://example.com/list?page=1&row=20"), anyInt());
        verifyNoMoreInteractions(jsoupClient);
    }

    @DisplayName("공지의 총 개수를 가져온다.")
    @Test
    void getTotalNoticeSize() throws IOException {
        // given
        Document doc = Jsoup.parse(TestFileLoader.loadHtmlFile("src/test/resources/notice/cse-notice-2024.html"));
        when(jsoupClient.get(anyString(), anyInt())).thenReturn(doc);
        String url = "https://cse.konkuk.ac.kr/cse/9962/subview.do";

        // when
        int totalNoticeSize = new LatestPageNoticeApiClient(jsoupClient).getTotalNoticeSize(url);

        // then
        assertThat(totalNoticeSize).isEqualTo(625);
    }

    private static Document docWithTotalCount(int total) {
        return Jsoup.parse("""
            <html><body><div class="util-search"><strong>%d</strong></div></body></html>
        """.formatted(total));
    }

    private static Document docWithTableRows(int n) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body><table class='board-table'><tbody>");
        IntStream.rangeClosed(1, n).forEach(i -> sb.append("<tr><td>").append(i).append("</td></tr>"));
        sb.append("</tbody></table></body></html>");
        return Jsoup.parse(sb.toString());
    }
}
