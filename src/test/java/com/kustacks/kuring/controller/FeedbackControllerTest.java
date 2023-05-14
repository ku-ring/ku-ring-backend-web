package com.kustacks.kuring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.kustacks.kuring.common.error.APIException;
import com.kustacks.kuring.feedback.common.dto.SaveFeedbackV1Request;
import com.kustacks.kuring.common.error.ErrorCode;
import com.kustacks.kuring.feedback.presentation.FeedbackController;
import com.kustacks.kuring.feedback.business.FeedbackService;
import com.kustacks.kuring.common.firebase.FirebaseService;
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.kustacks.kuring.ApiDocumentUtils.getDocumentRequest;
import static com.kustacks.kuring.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class})
@WebMvcTest(FeedbackController.class)
public class FeedbackControllerTest {

//    @Rule
//    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @MockBean
    private FirebaseService firebaseService;

    @MockBean
    private FeedbackService feedbackService;

    @Mock
    private FirebaseMessagingException firebaseMessagingException;

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }
    
    @DisplayName("피드백 저장 API - 성공")
    @Test
    public void saveFeedbackSuccessTest() throws Exception {

        String token = "TEST_TOKEN";
        String content = "테스트 피드백입니다.";

        SaveFeedbackV1Request requestDTO = new SaveFeedbackV1Request(token, content);

        String requestBody = objectMapper.writeValueAsString(requestDTO);

        // given
        /*
            firebaseService.verifyToken 및 feedbackService.insertFeedback은 성공 시 void 리턴.
         */

        // when
        ResultActions result = mockMvc.perform(post("/api/v1/feedback")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("isSuccess").value(true))
                .andExpect(jsonPath("resultMsg").value("성공"))
                .andExpect(jsonPath("resultCode").value(201))
                .andDo(document("save-feedback-success",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("id").type(JsonFieldType.STRING).description("FCM 토큰"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("피드백 내용. 5자 이상 256자 이하")
                        ),
                        responseFields(
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("resultMsg").type(JsonFieldType.STRING).description("결과 메세지"),
                                fieldWithPath("resultCode").type(JsonFieldType.NUMBER).description("결과 코드")
                        ))
                );
    }

    @DisplayName("피드백 저장 API - 실패 - 유효하지 않은 토큰")
    @Test
    public void saveFeedbackFailByInvalidTokenTest() throws Exception {
        // given
        String token = "INVALID_TOKEN";
        String content = "테스트 피드백입니다.";
        String requestBody = objectMapper.writeValueAsString(new SaveFeedbackV1Request(token, content));

        doThrow(new APIException(ErrorCode.API_FB_INVALID_TOKEN)).when(firebaseService).validationToken(token);

        // when
        ResultActions result = mockMvc.perform(post("/api/v1/feedback")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("isSuccess").value(false))
                .andExpect(jsonPath("resultMsg").value(ErrorCode.API_FB_INVALID_TOKEN.getMessage()))
                .andExpect(jsonPath("resultCode").value(ErrorCode.API_FB_INVALID_TOKEN.getHttpStatus().value()))
                .andDo(document("save-feedback-fail-invalid-token",
                        getDocumentRequest(),
                        getDocumentResponse())
                );
    }

    @DisplayName("피드백 저장 API - 실패 - 유효하지 않은 피드백 길이")
    @Test
    public void saveFeedbackFailByInvalidContentLength() throws Exception {
        // given
        String token = "TEST_TOKEN";
        String content = "5자미만";
        String requestBody = objectMapper.writeValueAsString(new SaveFeedbackV1Request(token, content));

        // when
        ResultActions result = mockMvc.perform(post("/api/v1/feedback")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("isSuccess").value(false))
                .andExpect(jsonPath("resultMsg").value(ErrorCode.API_FD_INVALID_CONTENT.getMessage()))
                .andExpect(jsonPath("resultCode").value(ErrorCode.API_FD_INVALID_CONTENT.getHttpStatus().value()))
                .andDo(document("save-feedback-fail-invalid-content-length",
                        getDocumentRequest(),
                        getDocumentResponse())
                );
    }
}
