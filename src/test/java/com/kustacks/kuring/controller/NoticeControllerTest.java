package com.kustacks.kuring.controller;

import com.kustacks.kuring.controller.dto.NoticeDTO;
import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.service.NoticeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import static com.kustacks.kuring.ApiDocumentUtils.getDocumentRequest;
import static com.kustacks.kuring.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(NoticeController.class)
@TestPropertySource("classpath:test-constants.properties")
public class NoticeControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private NoticeService noticeService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private final String articleId = "5b45b56";
    private final String postedDate = "20210504";
    private final String subject = "증명서 발급 시 본인 인증 방법 변경 안내";
    private final String categoryName = "bachelor";
    private final String baseUrl = "https://www.konkuk.ac.kr/do/MessageBoard/ArticleRead.do";
    private final String fullUrl = "https://www.konkuk.ac.kr/do/MessageBoard/ArticleRead.do?id=5b45b56";

    private String type;
    private int offset;
    private int max;

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @DisplayName("공지 API - 성공")
    @Test
    public void getNoticesSuccessTest() throws Exception {

        type = "bch";
        offset = 0;
        max = 10;

        List<NoticeDTO> noticeDTOList = new LinkedList<>();
        noticeDTOList.add(NoticeDTO.builder()
                .articleId(articleId)
                .postedDate(postedDate)
                .subject(subject)
                .categoryName(categoryName)
                .fullUrl(fullUrl)
                .build());

        given(noticeService.getNotices(categoryName, offset, max)).willReturn(noticeDTOList);

        // when
        ResultActions result = mockMvc.perform(get("/api/v1/notice")
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON)
                .queryParam("type", type)
                .queryParam("offset", String.valueOf(offset))
                .queryParam("max", String.valueOf(max)));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("isSuccess").value(true))
                .andExpect(jsonPath("resultMsg").value("성공"))
                .andExpect(jsonPath("resultCode").value(200))
                .andExpect(jsonPath("baseUrl").value(baseUrl))
                .andExpect(jsonPath("noticeList").exists())
                .andDo(document("notice-success",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestParameters(
                                        parameterWithName("type").description("공지 카테고리 키워드")
                                                .attributes(key("Constraints").value("쿠링 Notion > 서버 > API 가이드 > 메인화면 - 모든 공지 데이터 API > key=type의 value 항목 참고")),
                                        parameterWithName("offset").description("가져올 공지의 시작 인덱스")
                                                .attributes(key("Constraints").value("0 이상의 정수")),
                                        parameterWithName("max").description("가져올 공지 최대 개수")
                                                .attributes(key("Constraints").value("1 이상 30 이하의 정수"))
                                ),
                                responseFields(
                                        fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                        fieldWithPath("resultMsg").type(JsonFieldType.STRING).description("결과 메세지"),
                                        fieldWithPath("resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                        fieldWithPath("baseUrl").type(JsonFieldType.STRING).description("공지 확인할 수 있는 url의 공통 부분"),
                                        fieldWithPath("noticeList[].articleId").type(JsonFieldType.STRING).description("공지 ID"),
                                        fieldWithPath("noticeList[].postedDate").type(JsonFieldType.STRING).description("공지 게시일"),
                                        fieldWithPath("noticeList[].subject").type(JsonFieldType.STRING).description("공지 제목"),
                                        fieldWithPath("noticeList[].category").type(JsonFieldType.STRING).description("공지 카테고리명"),
                                        fieldWithPath("noticeList[].fullUrl").type(JsonFieldType.STRING).description("공지 링크")
                                ))

                );
    }

    @DisplayName("공지 API - 실패 - 잘못된 공지 카테고리")
    @Test
    public void getNoticesFailByInvalidTypeTest() throws Exception {
        type = "invalid-type";
        offset = 0;
        max = 20;

        // when
        ResultActions result = mockMvc.perform(get("/api/v1/notice")
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON)
                .queryParam("type", type)
                .queryParam("offset", String.valueOf(offset))
                .queryParam("max", String.valueOf(max)));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("isSuccess").value(false))
                .andExpect(jsonPath("resultMsg").value(ErrorCode.API_NOTICE_NOT_EXIST_CATEGORY.getMessage()))
                .andExpect(jsonPath("resultCode").value(HttpStatus.BAD_REQUEST.value()))
                .andDo(document("notice-fail-invalid-category",
                        getDocumentRequest(),
                        getDocumentResponse())
                );
    }

    @DisplayName("공지 API - 실패 - 잘못된 offset 혹은 max 파라미터 값")
    @Test
    public void getNoticesFailByInvalidOffsetTest() throws Exception {
        type = "bch";
        offset = -1;
        max = 20;

        // when
        ResultActions result = mockMvc.perform(get("/api/v1/notice")
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON)
                .queryParam("type", type)
                .queryParam("offset", String.valueOf(offset))
                .queryParam("max", String.valueOf(max)));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("isSuccess").value(false))
                .andExpect(jsonPath("resultMsg").value(ErrorCode.API_INVALID_PARAM.getMessage()))
                .andExpect(jsonPath("resultCode").value(HttpStatus.BAD_REQUEST.value()))
                .andDo(document("notice-fail-invalid-param",
                        getDocumentRequest(),
                        getDocumentResponse())
                );
    }
}
