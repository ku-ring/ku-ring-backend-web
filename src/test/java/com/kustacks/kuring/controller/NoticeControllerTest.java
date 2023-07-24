package com.kustacks.kuring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.kustacks.kuring.category.business.CategoryService;
import com.kustacks.kuring.category.common.dto.SubscribeCategoriesV1Request;
import com.kustacks.kuring.category.domain.Category;
import com.kustacks.kuring.category.domain.CategoryName;
import com.kustacks.kuring.common.error.APIException;
import com.kustacks.kuring.common.error.ErrorCode;
import com.kustacks.kuring.common.firebase.FirebaseService;
import com.kustacks.kuring.notice.business.NoticeService;
import com.kustacks.kuring.notice.common.dto.NoticeDto;
import com.kustacks.kuring.notice.common.dto.NoticeListResponse;
import com.kustacks.kuring.notice.presentation.NoticeController;
import com.kustacks.kuring.user.business.UserService;
import com.kustacks.kuring.user.facade.UserCommandFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import static com.kustacks.kuring.ApiDocumentUtils.getDocumentRequest;
import static com.kustacks.kuring.ApiDocumentUtils.getDocumentResponse;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(NoticeController.class)
public class NoticeControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private NoticeService noticeService;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private FirebaseService firebaseService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserCommandFacade userCommandFacade;

    @Mock
    private FirebaseMessagingException firebaseMessagingException;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private final String articleId = "5cw2e1";
    private final String postedDate = "20211016";
    private final String subject = "[학사] 2021년 학사 관련 공지";
    private final String categoryName = "bachelor";

    private String type;
    private int offset;
    private int max;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @DisplayName("공지 API - 성공")
    @Test
    void getNoticesSuccessTest() throws Exception {

        type = "bch";
        offset = 0;
        max = 10;

        List<NoticeDto> noticeDtoList = new LinkedList<>();
        noticeDtoList.add(NoticeDto.builder()
                .articleId(articleId)
                .postedDate(postedDate)
                .subject(subject)
                .url("url")
                .category(categoryName)
                .important(false)
                .build());

        NoticeListResponse noticeListResponse = new NoticeListResponse("https://www.konkuk.ac.kr/do/MessageBoard/ArticleRead.do", noticeDtoList);

        given(noticeService.getNotices(type, offset, max)).willReturn(noticeListResponse);

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
                .andExpect(jsonPath("baseUrl").exists())
                .andExpect(jsonPath("noticeList").exists())
                .andDo(document("notice-success",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParameters(
                                parameterWithName("type").description("공지 카테고리 키워드")
                                        .attributes(key("Constraints").value("bch, sch, emp, nat, stu, ind, nor, lib")),
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
                                fieldWithPath("noticeList[].important").type(JsonFieldType.BOOLEAN).description("공지 중요 유무"),
                                fieldWithPath("noticeList[].url").type(JsonFieldType.STRING).description("공지 주소")
                        ))

                );
    }

    @DisplayName("공지 API - 실패 - 잘못된 공지 카테고리")
    @Test
    void getNoticesFailByInvalidTypeTest() throws Exception {
        type = "invalid-type";
        offset = 0;
        max = 20;

        given(noticeService.getNotices(type, offset, max)).willThrow(new APIException(ErrorCode.API_NOTICE_NOT_EXIST_CATEGORY));

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
    void getNoticesFailByInvalidOffsetTest() throws Exception {
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


    @DisplayName("서버에서 제공하는 공지 카테고리 목록 제공 API - 성공")
    @Test
    void getSupportedCategoriesSuccessTest() throws Exception {

        List<Category> categories = new LinkedList<>();
        categories.add(new Category(CategoryName.BACHELOR));
        categories.add(new Category(CategoryName.EMPLOYMENT));

        List<CategoryName> categoryNames = new LinkedList<>();
        categoryNames.add(CategoryName.BACHELOR);
        categoryNames.add(CategoryName.EMPLOYMENT);

        // given
        given(categoryService.lookUpSupportedCategories()).willReturn(categoryNames);

        // when
        ResultActions result = mockMvc.perform(get("/api/v1/notice/categories")
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("isSuccess").value(true))
                .andExpect(jsonPath("resultMsg").value("성공"))
                .andExpect(jsonPath("resultCode").value(200))
                .andExpect(jsonPath("categories", hasSize(2)))
                .andExpect(jsonPath("categories[0]").value(categoryNames.get(0).getName()))
                .andExpect(jsonPath("categories[1]").value(categoryNames.get(1).getName()))
                .andDo(document("category-get-all-success",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParameters(),
                        responseFields(
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("resultMsg").type(JsonFieldType.STRING).description("결과 메세지"),
                                fieldWithPath("resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("categories").type(JsonFieldType.ARRAY).description("서버에서 지원하는 공지 카테고리 목록")
                        ))
                );
    }

    @DisplayName("특정 회원이 구독한 카테고리 목록 제공 API - 성공")
    @Test
    void getUserCategoriesSuccessTest() throws Exception {
        String token = "TEST_TOKEN";

        List<Category> categories = new LinkedList<>();
        categories.add(new Category(CategoryName.BACHELOR));
        categories.add(new Category(CategoryName.EMPLOYMENT));

        List<CategoryName> categoryNames = new LinkedList<>();
        categoryNames.add(CategoryName.BACHELOR);
        categoryNames.add(CategoryName.EMPLOYMENT);

        // given
        given(userService.lookUpUserCategories(token)).willReturn(categoryNames);

        // when
        ResultActions result = mockMvc.perform(get("/api/v1/notice/subscribe")
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON)
                .queryParam("id", token));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("isSuccess").value(true))
                .andExpect(jsonPath("resultMsg").value("성공"))
                .andExpect(jsonPath("resultCode").value(200))
                .andExpect(jsonPath("categories", hasSize(2)))
                .andExpect(jsonPath("categories[0]").value(categoryNames.get(0).getName()))
                .andExpect(jsonPath("categories[1]").value(categoryNames.get(1).getName()))
                .andDo(document("category-get-user-categories-success",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParameters(
                                parameterWithName("id").description("유효한 FCM 토큰")
                                        .attributes(key("Constraints").value(""))
                        ),
                        responseFields(
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("resultMsg").type(JsonFieldType.STRING).description("결과 메세지"),
                                fieldWithPath("resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("categories").type(JsonFieldType.ARRAY).description("해당 회원이 구독한 카테고리 목록")
                        ))
                );
    }

    @DisplayName("특정 회원이 구독한 카테고리 목록 제공 API - 실패 - 유효하지 않은 FCM 토큰")
    @Test
    void getUserCategoriesFailByInvalidTokenTest() throws Exception {
        // given
        String token = "INVALID_TOKEN";
        doThrow(new APIException(ErrorCode.API_FB_INVALID_TOKEN)).when(firebaseService).validationToken(token);

        // when
        ResultActions result = mockMvc.perform(get("/api/v1/notice/subscribe")
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON)
                .queryParam("id", token));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("isSuccess").value(false))
                .andExpect(jsonPath("resultMsg").value(ErrorCode.API_FB_INVALID_TOKEN.getMessage()))
                .andExpect(jsonPath("resultCode").value(ErrorCode.API_FB_INVALID_TOKEN.getHttpStatus().value()))
                .andExpect(jsonPath("categories").doesNotExist())
                .andDo(document("category-get-user-categories-fail-invalid-token",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));
    }

    @DisplayName("특정 회원이 구독한 카테고리 목록 제공 API - 실패 - 필수 파라미터 누락")
    @Test
    void getUserCategoriesFailByMissingParamTest() throws Exception {

        // when
        ResultActions result = mockMvc.perform(get("/api/v1/notice/subscribe")
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("isSuccess").value(false))
                .andExpect(jsonPath("resultMsg").value(ErrorCode.API_MISSING_PARAM.getMessage()))
                .andExpect(jsonPath("resultCode").value(ErrorCode.API_MISSING_PARAM.getHttpStatus().value()))
                .andExpect(jsonPath("categories").doesNotExist())
                .andDo(document("category-get-user-categories-fail-missing-param",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));
    }

    @DisplayName("특정 회원의 구독 카테고리 편집 API - 성공")
    @Test
    void subscribeCategoriesSuccessTest() throws Exception {
        // given
        SubscribeCategoriesV1Request subscribeCategoriesRequest = new SubscribeCategoriesV1Request("TEST_TOKEN", List.of("bachelor", "student"));
        doNothing().when(firebaseService).validationToken(anyString());
        doNothing().when(userCommandFacade).editSubscribeCategories(anyString(), any());

        // when
        ResultActions result = mockMvc.perform(post("/api/v1/notice/subscribe")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(subscribeCategoriesRequest)));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("isSuccess").value(true))
                .andExpect(jsonPath("resultMsg").value("성공"))
                .andExpect(jsonPath("resultCode").value(201))
                .andDo(document("category-subscribe-categories-success",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("id").type(JsonFieldType.STRING).description("FCM 토큰"),
                                fieldWithPath("categories").type(JsonFieldType.ARRAY).description("알림을 받을 공지 카테고리 목록")
                        ),
                        responseFields(
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("resultMsg").type(JsonFieldType.STRING).description("결과 메세지"),
                                fieldWithPath("resultCode").type(JsonFieldType.NUMBER).description("결과 코드")
                        ))
                );
    }

    @DisplayName("특정 회원의 구독 카테고리 편집 API - 실패 - 유효하지 않은 토큰")
    @Test
    void subscribeCategoriesFailByInvalidToken() throws Exception {

        String token = "INVALID_TOKEN";

        List<String> categories = new LinkedList<>();
        categories.add("bachelor");
        categories.add("student");

        SubscribeCategoriesV1Request requestDTO = new SubscribeCategoriesV1Request(token, categories);

        // given
        given(userService.getUserByToken(token)).willReturn(null);
        doThrow(new APIException(ErrorCode.API_FB_INVALID_TOKEN)).when(userCommandFacade).editSubscribeCategories(anyString(), any());

        // when
        ResultActions result = mockMvc.perform(post("/api/v1/notice/subscribe")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("isSuccess").value(false))
                .andExpect(jsonPath("resultMsg").value(ErrorCode.API_FB_INVALID_TOKEN.getMessage()))
                .andExpect(jsonPath("resultCode").value(ErrorCode.API_FB_INVALID_TOKEN.getHttpStatus().value()))
                .andDo(document("category-subscribe-categories-fail-invalid-token",
                        getDocumentRequest(),
                        getDocumentResponse())
                );
    }

    @DisplayName("특정 회원의 구독 카테고리 편집 API - 실패 - 요청 body에 필수 json 필드 누락")
    @Test
    void subscribeCategoriesFailByMissingJsonField() throws Exception {
        // given
        String requestBody = "{\"categories\": [\"bachelor\", \"student\"]}";

        // when
        ResultActions result = mockMvc.perform(post("/api/v1/notice/subscribe")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("isSuccess").value(false))
                .andExpect(jsonPath("resultMsg").value(ErrorCode.API_MISSING_PARAM.getMessage()))
                .andExpect(jsonPath("resultCode").value(ErrorCode.API_MISSING_PARAM.getHttpStatus().value()))
                .andDo(document("category-subscribe-categories-fail-missing-json-field",
                        getDocumentRequest(),
                        getDocumentResponse())
                );
    }

    @DisplayName("특정 회원의 구독 카테고리 편집 API - 실패 - 서버에서 지원하지 않는 카테고리를 수신")
    @Test
    void subscribeCategoriesFailByNotSupportedCategory() throws Exception {

        String token = "TEST_TOKEN";

        List<String> categories = new LinkedList<>();
        categories.add("bachelor");
        categories.add("invalid-category");

        SubscribeCategoriesV1Request requestDTO = new SubscribeCategoriesV1Request(token, categories);

        // given
        doThrow(new APIException(ErrorCode.API_INVALID_PARAM)).when(userCommandFacade).editSubscribeCategories(any(), any());

        // when
        ResultActions result = mockMvc.perform(post("/api/v1/notice/subscribe")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("isSuccess").value(false))
                .andExpect(jsonPath("resultMsg").value(ErrorCode.API_INVALID_PARAM.getMessage()))
                .andExpect(jsonPath("resultCode").value(ErrorCode.API_INVALID_PARAM.getHttpStatus().value()))
                .andDo(document("category-subscribe-categories-fail-not-supported-category",
                        getDocumentRequest(),
                        getDocumentResponse())
                );

    }

    @DisplayName("특정 회원의 구독 카테고리 편집 API - 실패 - FCM 오류로 인한 구독 및 구독 취소 실패")
    @Test
    void subscribeCategoriesFailByFCMError() throws Exception {

        String token = "TEST_TOKEN";

        List<String> categories = new LinkedList<>();
        categories.add("bachelor");
        categories.add("student");

        SubscribeCategoriesV1Request request = new SubscribeCategoriesV1Request(token, categories);

        // given
        doThrow(new APIException(ErrorCode.API_FB_CANNOT_EDIT_CATEGORY)).when(userCommandFacade).editSubscribeCategories(any(), any());

        // when
        ResultActions result = mockMvc.perform(post("/api/v1/notice/subscribe")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("isSuccess").value(false))
                .andExpect(jsonPath("resultMsg").value(ErrorCode.API_FB_CANNOT_EDIT_CATEGORY.getMessage()))
                .andExpect(jsonPath("resultCode").value(ErrorCode.API_FB_CANNOT_EDIT_CATEGORY.getHttpStatus().value()))
                .andDo(document("category-subscribe-categories-fail-firebase-error",
                        getDocumentRequest(),
                        getDocumentResponse())
                );
    }
}
