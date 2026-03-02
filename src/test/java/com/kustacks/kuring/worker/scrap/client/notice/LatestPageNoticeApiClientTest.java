package com.kustacks.kuring.worker.scrap.client.notice;

import com.kustacks.kuring.support.TestFileLoader;
import com.kustacks.kuring.worker.scrap.client.NormalJsoupClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.dto.ScrapingResultDto;
import com.kustacks.kuring.common.exception.InternalLogicException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class LatestPageNoticeApiClientTest {

    @Mock
    private NormalJsoupClient jsoupClient;
    @InjectMocks
    private LatestPageNoticeApiClient apiClient;

    @DisplayName("tr merge 및 총 tr 갯수 검증 테스트")
    @Test
    void requestAll_merge_trs_and_match_total_count() throws Exception {
        // given
        DeptInfo deptInfo = mock(DeptInfo.class);
        when(deptInfo.createUndergraduateViewUrl()).thenReturn("https://example.com/view");

        when(deptInfo.createUndergraduateRequestUrl(anyInt(), anyInt()))
                .thenAnswer(arg -> {
                    int page = arg.getArgument(0);
                    int row = arg.getArgument(1);
                    return "https://example.com/list?page=" + page + "&row=" + row;
                });

        int total = 313; // (100+13) + 100 + 100
        String totalUrl = "https://example.com/list?page=1&row=1";
        String page1 = "https://example.com/list?page=1&row=100";
        String page2 = "https://example.com/list?page=2&row=100";
        String page3 = "https://example.com/list?page=3&row=100";

        when(jsoupClient.get(eq(totalUrl), anyInt())) // 총 300개 공지 갯수 반환 -> 총 3page
                .thenReturn(loadFile("cse-notice_total_only.html"));
        when(jsoupClient.get(eq(page1), anyInt())) // 중요 공지 13 + 일반 공지 100 (page1)
                .thenReturn(loadFile("cse-notice-2026-page1.html"));
        when(jsoupClient.get(eq(page2), anyInt())) // 일반 공지 100 (page 2)
                .thenReturn(loadFile("cse-notice-2026-page2.html"));
        when(jsoupClient.get(eq(page3), anyInt())) // 일반 공지 100 (page 3)
                .thenReturn(loadFile("cse-notice-2026-page3.html"));

        // when
        List<ScrapingResultDto> result = apiClient.requestAll(deptInfo);

        // then
        assertThat(result).hasSize(1);

        Document merged = result.get(0).getDocument();
        assertThat(merged.select(".board-table > tbody > tr").size()).isEqualTo(total);

        // 호출을 모두 했는지 검증
        assertAll(
                () -> verify(jsoupClient).get(eq(totalUrl), anyInt()),
                () -> verify(jsoupClient).get(eq(page1), anyInt()),
                () -> verify(jsoupClient).get(eq(page2), anyInt()),
                () -> verify(jsoupClient).get(eq(page3), anyInt())
        );
    }

    @DisplayName("base 페이지에 tbody가 없으면 InternalLogicException 에러를 발생시킨다.")
    @Test
    void requestAll_throw_notice_scrap_exception_when_no_tbody() throws Exception {
        // given
        DeptInfo deptInfo = mock(DeptInfo.class);

        when(deptInfo.createUndergraduateRequestUrl(anyInt(), anyInt()))
                .thenAnswer(arg -> "https://example.com/list?page=" + arg.getArgument(0) + "&row=" + arg.getArgument(1));

        String totalUrl = "https://example.com/list?page=1&row=1";
        String firstUrl = "https://example.com/list?page=1&row=100";
        String secondUrl = "https://example.com/list?page=2&row=100";

        when(jsoupClient.get(eq(totalUrl), anyInt()))
                .thenReturn(loadFile("cse-notice_total_only.html"));

        // 총 공지 갯수가 300개였으나 baseDoc에 tbody 없는 경우
        when(jsoupClient.get(eq(firstUrl), anyInt()))
                .thenReturn(loadFile("cse-notice_no_tbody.html"));
        // 두번째 페이지는 tbody 존재
        when(jsoupClient.get(eq(secondUrl), anyInt()))
                .thenReturn(loadFile("cse-notice-2026-page2.html"));

        // when & then
        assertThatThrownBy(() -> apiClient.requestAll(deptInfo))
                .isInstanceOf(InternalLogicException.class);
    }

    @DisplayName("중간 페이지에서 tr이 비면 break 한다(이후 페이지 호출 안 함)")
    @Test
    void requestAll_break_when_page_has_no_trs() throws Exception {
        DeptInfo deptInfo = mock(DeptInfo.class);
        when(deptInfo.createUndergraduateViewUrl()).thenReturn("https://example.com/view");
        when(deptInfo.createUndergraduateRequestUrl(anyInt(), anyInt()))
                .thenAnswer(arg -> "https://example.com/list?page=" + arg.getArgument(0) + "&row=" + arg.getArgument(1));

        int total = 113; // page1에서 일반 공지 100 + 중요 공지 13
        String totalUrl = "https://example.com/list?page=1&row=1";
        String page1 = "https://example.com/list?page=1&row=100";
        String page2 = "https://example.com/list?page=2&row=100";
        String page3 = "https://example.com/list?page=3&row=100";

        when(jsoupClient.get(eq(totalUrl), anyInt())) // 총 300개 공지 개수 반환 -> 3page
                .thenReturn(loadFile("cse-notice_total_only.html"));
        when(jsoupClient.get(eq(page1), anyInt())) // 중요 공지 13 + 일반 공지 100 (page 1)
                .thenReturn(loadFile("cse-notice-2026-page1.html"));
        when(jsoupClient.get(eq(page2), anyInt())) // 여기서 break 유도 (page 2)
                .thenReturn(loadFile("cse-notice_empty_tbody.html"));

        List<ScrapingResultDto> result = apiClient.requestAll(deptInfo);

        assertThat(result).hasSize(1);
        Document merged = result.get(0).getDocument();
        assertThat(merged.select(".board-table > tbody > tr").size()).isEqualTo(total);

        // 호출 횟수/호출 여부 검증
        assertAll(
                () -> verify(jsoupClient, times(1)).get(eq(totalUrl), anyInt()),
                () -> verify(jsoupClient, times(1)).get(eq(page1), anyInt()),
                () -> verify(jsoupClient, times(1)).get(eq(page2), anyInt()),
                () -> verify(jsoupClient, never()).get(eq(page3), anyInt())
        );
    }

    @DisplayName("최신 1페이지 요청 시 dto 1개를 반환한다")
    @Test
    void request_return_single_dto() throws Exception {
        // given
        DeptInfo deptInfo = mock(DeptInfo.class);
        when(deptInfo.createUndergraduateViewUrl()).thenReturn("https://example.com/view");
        when(deptInfo.createUndergraduateRequestUrl(eq(1), anyInt()))
                .thenReturn("https://example.com/list?page=1&row=100");

        when(jsoupClient.get(eq("https://example.com/list?page=1&row=100"), anyInt()))
                .thenReturn(loadFile("cse-notice-2026-page1.html"));

        // when
        List<ScrapingResultDto> result = apiClient.request(deptInfo);

        // then
        assertAll(
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result.get(0).getDocument()).isNotNull(),
                () -> verify(jsoupClient, times(1)).get(eq("https://example.com/list?page=1&row=100"), anyInt())
        );
    }

    @DisplayName("공지의 총 개수를 가져온다.")
    @Test
    void getTotalNoticeSize() throws IOException {
        // given
        Document doc = Jsoup.parse(TestFileLoader.loadHtmlFile("src/test/resources/notice/cse-notice-2024.html"));
        when(jsoupClient.get(anyString(), anyInt())).thenReturn(doc);
        String url = "https://cse.konkuk.ac.kr/cse/9962/subview.do";

        // when
        int totalNoticeSize = apiClient.getTotalNoticeSize(url);

        // then
        assertThat(totalNoticeSize).isEqualTo(625);
    }

    private static Document loadFile(String fileName) throws IOException {
        return Jsoup.parse(TestFileLoader.loadHtmlFile(
                "src/test/resources/notice/" + fileName
        ));
    }
}
