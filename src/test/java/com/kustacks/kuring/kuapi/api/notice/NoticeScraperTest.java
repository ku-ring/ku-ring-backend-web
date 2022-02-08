package com.kustacks.kuring.kuapi.api.notice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustacks.kuring.config.JsonConfig;
import com.kustacks.kuring.config.MappedBeanConfig;
import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.error.InternalLogicException;
import com.kustacks.kuring.kuapi.api.staff.NormalJsoupClient;
import com.kustacks.kuring.kuapi.deptinfo.DeptInfo;
import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.liberal_art.ChineseDept;
import com.kustacks.kuring.kuapi.deptinfo.liberal_art.KoreanDept;
import com.kustacks.kuring.kuapi.deptinfo.real_estate.RealEstateDept;
import com.kustacks.kuring.kuapi.notice.dto.response.CommonNoticeFormatDTO;
import com.kustacks.kuring.kuapi.scrap.NoticeScraper;
import com.kustacks.kuring.kuapi.scrap.parser.LegacyPageNoticeHTMLParser;
import com.kustacks.kuring.kuapi.scrap.parser.RealEstateNoticeHTMLParser;
import com.kustacks.kuring.kuapi.scrap.parser.RecentPageNoticeHTMLParser;
import com.kustacks.kuring.kuapi.scrap.parser.RecentPageNoticeHTMLParser2;
import org.junit.jupiter.api.*;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.LinkedMultiValueMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockserver.matchers.Times.exactly;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringJUnitConfig({
        NoticeScraper.class,
        LegacyPageNoticeAPIClient.class, RecentPageNoticeAPIClient.class, RealEstateNoticeAPIClient.class,
        LegacyPageNoticeHTMLParser.class, RecentPageNoticeHTMLParser.class, RecentPageNoticeHTMLParser2.class, RealEstateNoticeHTMLParser.class,
        NormalJsoupClient.class,
        KoreanDept.class, ChineseDept.class, RealEstateDept.class,
        JsonConfig.class, MappedBeanConfig.ScrapNoticeMappedBeanConfig.class
})
@TestPropertySource("classpath:test-constants.properties")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class NoticeScraperTest {

    private static ClientAndServer mockServer;
    private static final int MOCK_SERVER_PORT = 9000;

    private final String legacyRequestUrl;
    private final String recentRequestUrl;
    private final String realEstateRequestUrl;

    private final NoticeScraper noticeScraper;

    private final KoreanDept legacyDept;
    private final ChineseDept chineseDept;
    private final RealEstateDept realEstateDept;

    private final String koreanFolderName = "korean";
    private final String chineseFolderName = "chinese";
    private final String realEstateFolderName = "real_estate";

    private final ObjectMapper objectMapper;

    public NoticeScraperTest(@Value("${notice.legacy-url}") String legacyPageUrl,
                             @Value("${notice.recent-url}") String recentPageUrl,
                             @Value("${notice.real-estate-url}") String realEstateUrl,
                             NoticeScraper noticeScraper,
                             KoreanDept koreanDept,
                             ChineseDept chineseDept,
                             RealEstateDept realEstateDept,
                             ObjectMapper objectMapper) {

        this.noticeScraper = noticeScraper;
        this.legacyDept = koreanDept;
        this.chineseDept = chineseDept;
        this.realEstateDept = realEstateDept;
        this.objectMapper = objectMapper;

        this.legacyRequestUrl = legacyPageUrl + "?forum=" + chineseDept.getNoticeScrapInfo().getForumIds().get(0);

        String siteId = legacyDept.getNoticeScrapInfo().getSiteId();
        String boardSeq = legacyDept.getNoticeScrapInfo().getBoardSeqs().get(0);
        String menuSeq = legacyDept.getNoticeScrapInfo().getMenuSeqs().get(0);
        this.recentRequestUrl = recentPageUrl + "?siteId=" + siteId + "&boardSeq=" + boardSeq + "&menuSeq=" + menuSeq;

        this.realEstateRequestUrl = realEstateUrl;
    }

    @BeforeAll
    static void setUp() {
        mockServer = ClientAndServer.startClientAndServer(MOCK_SERVER_PORT);
    }

    @AfterAll
    static void cleanUp() {
        mockServer.stop();
    }

    @Nested
    @DisplayName("성공")
    class Success {

        @DisplayName("Legacy")
        @Test
        void successForLegacy() throws URISyntaxException, IOException {
            URI uri = new URI(legacyRequestUrl);
            String requestPath = uri.getPath();
            testTemplate(requestPath, chineseDept, 1, chineseFolderName);
        }

        @DisplayName("Recent")
        @Test
        void successForRecent() throws URISyntaxException, IOException {
            URI uri = new URI(recentRequestUrl);
            String requestPath = uri.getPath();
            testTemplate(requestPath, legacyDept, 2, koreanFolderName);
        }

        @DisplayName("RealEstate")
        @Test
        void successForRealEstate() throws URISyntaxException, IOException {
            URI uri = new URI(realEstateRequestUrl);
            String requestPath = uri.getPath();
            testTemplate(requestPath, realEstateDept, 1, realEstateFolderName);
        }

        private void testTemplate(String requestPath, DeptInfo deptInfo, int expectTime, String folderName) throws IOException {

            // given
            createExpectationForSuccess(requestPath, expectTime, folderName);

            // when
            List<CommonNoticeFormatDTO> noticeDTOList = noticeScraper.scrap(deptInfo);

            // then
            int correct = 0;
            List<CommonNoticeFormatDTO> answers = getAnswers("notice/noticepages/" + folderName + "/answers");
            for (CommonNoticeFormatDTO result : noticeDTOList) {
                for (CommonNoticeFormatDTO answer : answers) {
                    if(compareDTO(result, answer)) {
                        System.out.println(result.getArticleId());
                        System.out.println(result.getPostedDate());
                        System.out.println(result.getSubject());
                        System.out.println();
                        ++correct;
                        break;
                    }
                }
            }

            assertEquals(answers.size(), correct);
        }
        
        private void createExpectationForSuccess(String requestPath, int expectTime, String folderName) throws IOException {

            String responseBody = readHTML("notice/noticepages/" + folderName + "/page.html");

            HttpRequest request = request().withMethod("GET").withPath(requestPath);

            HttpResponse response = response().withStatusCode(200)
                    .withContentType(MediaType.TEXT_HTML_UTF_8)
                    .withBody(responseBody);

            new MockServerClient("127.0.0.1", MOCK_SERVER_PORT)
                    .when(request, exactly(expectTime))
                    .respond(response);
        }

        private List<CommonNoticeFormatDTO> getAnswers(String answerPath) {

            List<CommonNoticeFormatDTO> noticeDTOList = new LinkedList<>();
            int idx=1;
            while(true) {
                try {
                    String answerStr = readAnswer(answerPath, idx);
                    noticeDTOList.add(objectMapper.readValue(answerStr, CommonNoticeFormatDTO.class));
                    ++idx;
                } catch(IOException e) {
                    return noticeDTOList;
                }
            }
        }
    }

    @Nested
    @DisplayName("실패")
    class Fail {

        @Nested
        @DisplayName("HTML 구조 달라짐")
        class HTMLDiff {

            /*
                NoticeHTMLParser 실패 테스트
            */
            
            // btn_list_next02를 다른 문자열로 바꿈 (NoticeAPIClient 측에서 발생하는 파싱 에러)
            @Test
            @DisplayName("Legacy")
            void failByHTMLDiffForLegacy() throws IOException, URISyntaxException {
                URI uri = new URI(legacyRequestUrl);
                String requestPath = uri.getPath();
                testTemplate(requestPath, chineseDept, 1, chineseFolderName);
            }

            // 첫 번째 공지 subject 아이디 갖는 td 태그 지움 (HTMLParser 에서 발생하는 파싱 에러)
            @Test
            @DisplayName("Recent")
            void failByHTMLDiffForRecent() throws IOException, URISyntaxException {
                URI uri = new URI(recentRequestUrl);
                String requestPath = uri.getPath();
                testTemplate(requestPath, legacyDept, 2, koreanFolderName);
            }

            // 첫 번째 공지의 wr_id를 id로 바꿈 (HTMLParser 에서 발생하는 파싱 에러)
            @Test
            @DisplayName("RealEstate")
            void failByHTMLDiffForRealEstate() throws IOException, URISyntaxException {
                URI uri = new URI(realEstateRequestUrl);
                String requestPath = uri.getPath();
                testTemplate(requestPath, realEstateDept, 1, realEstateFolderName);
            }

            private void testTemplate(String requestPath, DeptInfo deptInfo, int expectTime, String folderName) throws IOException {

                // given
                createExpectationForHTMLDiff(requestPath, expectTime, folderName);

                // when, then
                InternalLogicException e = assertThrows(InternalLogicException.class, () -> noticeScraper.scrap(deptInfo));
                assertEquals(ErrorCode.NOTICE_SCRAPER_CANNOT_PARSE, e.getErrorCode());
            }

            private void createExpectationForHTMLDiff(String requestPath, int expectTime, String folderName) throws IOException {

                String responseBody = readHTML("notice/noticepages/" + folderName + "/wrong.html");

                HttpRequest request = request().withMethod("GET").withPath(requestPath);

                HttpResponse response = response().withStatusCode(200)
                        .withContentType(MediaType.TEXT_HTML_UTF_8)
                        .withBody(responseBody);

                new MockServerClient("127.0.0.1", MOCK_SERVER_PORT)
                        .when(request, exactly(expectTime))
                        .respond(response);
            }
        }

        @Nested
        @DisplayName("API 요청에 대한 잘못된 응답")
        class APIError {

            /*
                 NoticeAPIClient 실패 테스트
             */
            @DisplayName("Legacy")
            @Test
            void failByAPIErrorForLegacy() throws URISyntaxException {
                URI uri = new URI(legacyRequestUrl);
                String requestPath = uri.getPath();
                testTemplate(requestPath, chineseDept);
            }

            @Test
            @DisplayName("Recent")
            void failByAPIErrorForRecent() throws URISyntaxException {
                URI uri = new URI(recentRequestUrl);
                String requestPath = uri.getPath();
                testTemplate(requestPath, legacyDept);
            }

            @Test
            @DisplayName("RealEstate")
            void failByAPIErrorForRealEstate() throws URISyntaxException {
                URI uri = new URI(realEstateRequestUrl);
                String requestPath = uri.getPath();
                testTemplate(requestPath, realEstateDept);
            }

            void testTemplate(String requestPath, DeptInfo deptInfo) {

                // given
                createExpectationForAPIError(requestPath);

                // when, then
                InternalLogicException e = assertThrows(InternalLogicException.class, () -> noticeScraper.scrap(deptInfo));
                assertEquals(ErrorCode.NOTICE_SCRAPER_CANNOT_SCRAP, e.getErrorCode());
            }

            private void createExpectationForAPIError(String requestPath) {

                HttpRequest request = request().withMethod("GET").withPath(requestPath);

                HttpResponse response = response().withStatusCode(500);

                new MockServerClient("127.0.0.1", MOCK_SERVER_PORT)
                        .when(request, exactly(1))
                        .respond(response);
            }
        }
    }


    private String readHTML(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        return resourceToString(resource.getInputStream());
    }

    private String readAnswer(String path, int idx) throws IOException {
        ClassPathResource resource = new ClassPathResource(path + "/" + idx + ".json");
        return resourceToString(resource.getInputStream());
    }

    private String resourceToString(InputStream inputStream) throws IOException {
        return FileCopyUtils.copyToString(new InputStreamReader(inputStream));
    }

    private boolean compareDTO(CommonNoticeFormatDTO a, CommonNoticeFormatDTO b) {
        return Objects.equals(a.getArticleId(), b.getArticleId());
    }
}
