package com.kustacks.kuring.worker.scrap.client.notice;

import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.support.IntegrationTestSupport;
import com.kustacks.kuring.worker.scrap.client.auth.ParsingKuisAuthManager;
import com.kustacks.kuring.worker.scrap.client.notice.property.KuisNoticeProperties;
import com.kustacks.kuring.worker.update.notice.dto.request.BachelorKuisNoticeInfo;
import com.kustacks.kuring.worker.update.notice.dto.response.CommonNoticeFormatDto;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.client.ExpectedCount.times;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@DisplayName("클라이언트 : KUIS 종합정보시스템 공지")
class KuisNoticeApiClientTest extends IntegrationTestSupport {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private KuisNoticeProperties kuisNoticeProperties;

    @Autowired
    private KuisNoticeApiClient kuisNoticeAPIClient;

    @Autowired
    private BachelorKuisNoticeInfo bachelorKuisNoticeInfo;

    @MockBean
    private ParsingKuisAuthManager kuisAuthManager;

    private MockRestServiceServer server;

    @BeforeEach
    void setUpBefore() {
        server = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    @DisplayName("kuis 공지사항을 성공적으로 스크랩 해온다")
    void success() {
        // given
        String notice1ArticleId = "5b49a79";
        String notice1PostedDate = "20211220";
        String notice1Subject = "2022학년도 다,부,연계,연합,융합전공 신청(포기) 안내 (2021. 12. 20. 수정)";

        String notice2ArticleId = "5b49a74";
        String notice2PostedDate = "20211219";
        String notice2Subject = "2022학년도 전과 신청 안내 (2021. 12. 20. 수정)";

        String expectedResponseBody = "{\"DS_LIST\": [" +
                "{\"POSTED_DT\":\"" + notice1PostedDate + "\",\"SUBJECT\":\"" + notice1Subject + "\",\"ARTICLE_ID\":\"" + notice1ArticleId + "\"}," +
                "{\"POSTED_DT\":\"" + notice2PostedDate + "\",\"SUBJECT\":\"" + notice2Subject + "\",\"ARTICLE_ID\":\"" + notice2ArticleId + "\"}" +
                "]}";

        server.expect(times(1), requestTo(kuisNoticeProperties.requestUrl()))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess().contentType(MediaType.APPLICATION_JSON).body(expectedResponseBody));

        // when
        List<CommonNoticeFormatDto> notices = kuisNoticeAPIClient
                .request(bachelorKuisNoticeInfo);

        // then
        assertAll(
                () -> server.verify(),
                () -> assertThat(notices).hasSize(2)
                        .extracting("articleId", "postedDate", "subject")
                        .containsExactlyInAnyOrder(
                                tuple(notice1ArticleId, notice1PostedDate, notice1Subject),
                                tuple(notice2ArticleId, notice2PostedDate, notice2Subject)
                        )
        );
    }

    @Test
    @DisplayName("kuis 공지서버에 문제가 있는 경우에는 서버 에러로 간주한다")
    void fail_by_kuis_server_error() {
        // given
        String testSession = "JSESSIONID=SESSION_ID";
        given(kuisAuthManager.getSessionId()).willReturn(testSession);

        server.expect(times(3), requestTo(kuisNoticeProperties.requestUrl()))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withServerError());

        // when
        ThrowableAssert.ThrowingCallable actual =
                () -> kuisNoticeAPIClient.request(bachelorKuisNoticeInfo);

        // then
        assertThatThrownBy(actual)
                .isInstanceOf(InternalLogicException.class);
    }

    @Test
    @DisplayName("kuis 서버의 응답에 body가 없거는경우 서버 에러로 간주한다")
    void failByNoResponseBody() {
        // given
        String testSession = "JSESSIONID=SESSION_ID";
        given(kuisAuthManager.getSessionId()).willReturn(testSession);

        server.expect(times(3), requestTo(kuisNoticeProperties.requestUrl()))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess().contentType(MediaType.APPLICATION_JSON));

        // when
        ThrowableAssert.ThrowingCallable actual =
                () -> kuisNoticeAPIClient.request(bachelorKuisNoticeInfo);

        // then
        assertThatThrownBy(actual)
                .isInstanceOf(InternalLogicException.class);
    }

    @Test
    @DisplayName("kuis 서버의 응답 body에 DS_LIST 필드가 없는 경우 서버 에러로 간주한다")
    void failByNoDsListField() {
        // given
        String badResponseBody = "{\"UNKNOWN\": []}";
        String testSession = "JSESSIONID=SESSION_ID";

        given(kuisAuthManager.getSessionId()).willReturn(testSession);
        server.expect(times(3), requestTo(kuisNoticeProperties.requestUrl()))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess().contentType(MediaType.APPLICATION_JSON).body(badResponseBody));

        // when
        ThrowableAssert.ThrowingCallable actual =
                () -> kuisNoticeAPIClient.request(bachelorKuisNoticeInfo);

        // then
        assertThatThrownBy(actual)
                .isInstanceOf(InternalLogicException.class);
    }
}
