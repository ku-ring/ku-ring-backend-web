package com.kustacks.kuring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.kustacks.kuring.controller.dto.UserEnrollRequestDTO;
import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.persistence.user.User;
import com.kustacks.kuring.service.FirebaseService;
import com.kustacks.kuring.service.UserService;
import org.junit.jupiter.api.*;
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

import static com.kustacks.kuring.ApiDocumentUtils.getDocumentRequest;
import static com.kustacks.kuring.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(UserController.class)
@TestPropertySource("classpath:test-constants.properties")
public class UserControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private FirebaseService firebaseService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Nested
    @DisplayName("성공")
    class Success {

        @Nested
        @DisplayName("유저 FCM 토큰 등록 API")
        class UserEnroll {

            /* 이 테스트에서 유저 등록 API의 성공 케이스의 대부분의 snippet을 만들기 때문에
               유저 등록 API 성공 테스트들 중 첫 번째 순서로 실행되어야 한다.
            */
            @Test
            @Order(1)
            @DisplayName("이미 존재하는 FCM 토큰")
            void enrollAlreadyExistUserSuccess() throws Exception {

                // given
                // firebaseService.verifyToken은 원래 아무것도 반환히지 않음. 따라서 mocking 안해줘도 될듯
                String fcmToken = "EXIST_TOKEN";
                UserEnrollRequestDTO requestBody = new UserEnrollRequestDTO(fcmToken);
                given(userService.enrollUserToken(fcmToken)).willReturn(null);

                // when
                ResultActions result = mockMvc.perform(put("/api/v1/user")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)));

                // then
                result.andExpect(status().isOk())
                        .andExpect(jsonPath("isSuccess").value(true))
                        .andExpect(jsonPath("resultMsg").value("성공"))
                        .andExpect(jsonPath("resultCode").value(HttpStatus.NO_CONTENT.value()))
                        .andDo(document("enroll-user-success",
                                getDocumentRequest(),
                                requestFields(
                                        fieldWithPath("id").type(JsonFieldType.STRING).description("클라이언트의 FCM 토큰")
                                ),
                                responseFields(
                                        fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                        fieldWithPath("resultMsg").type(JsonFieldType.STRING).description("결과 메세지"),
                                        fieldWithPath("resultCode").type(JsonFieldType.NUMBER).description("결과 코드")
                                                .attributes(key("Note").value("이미 등록된 토큰이면 204, 아니면 201"))
                                )))
                        .andDo(document("enroll-already-exist-user-success",
                                getDocumentResponse()));
            }

            @Test
            @Order(2)
            @DisplayName("새로운 FCM 토큰")
            void enrollNewUserSuccess() throws Exception {

                // given
                String fcmToken = "NEW_TOKEN";
                UserEnrollRequestDTO requestBody = new UserEnrollRequestDTO(fcmToken);
                given(userService.enrollUserToken(fcmToken)).willReturn(User.builder().token(fcmToken).build());

                // when
                ResultActions result = mockMvc.perform(put("/api/v1/user")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)));

                // then
                result.andExpect(status().isOk())
                        .andExpect(jsonPath("isSuccess").value(true))
                        .andExpect(jsonPath("resultMsg").value("성공"))
                        .andExpect(jsonPath("resultCode").value(HttpStatus.CREATED.value()))
                        .andDo(document("enroll-new-user-success",
                                getDocumentResponse()));
            }
        }
    }

    @Nested
    @DisplayName("실패")
    class Fail {

        @Test
        @DisplayName("유효하지 않은 유저 FCM 토큰")
        void enrollUserFailByInvalidFCMToken() throws Exception {

            // given
            String fcmToken = "INVALID_TOKEN";
            UserEnrollRequestDTO requestBody = new UserEnrollRequestDTO(fcmToken);
            doThrow(FirebaseMessagingException.class).when(firebaseService).verifyToken(fcmToken);

            // when
            ResultActions result = mockMvc.perform(put("/api/v1/user")
                    .characterEncoding(StandardCharsets.UTF_8)
//                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("isSuccess").value(false))
                    .andExpect(jsonPath("resultMsg").value(ErrorCode.API_INVALID_PARAM.getMessage()))
                    .andExpect(jsonPath("resultCode").value(ErrorCode.API_INVALID_PARAM.getHttpStatus().value()))
                    .andDo(document("enroll-user-fail-invalid-token",
                            getDocumentRequest(),
                            getDocumentResponse()));
        }
    }
}
