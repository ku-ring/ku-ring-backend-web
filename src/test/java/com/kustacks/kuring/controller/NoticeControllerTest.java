package com.kustacks.kuring.controller;

import com.kustacks.kuring.controller.dto.NoticeDTO;
import com.kustacks.kuring.service.NoticeService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit4.SpringRunner;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(NoticeController.class)
public class NoticeControllerTest {

    // gradle을 기반으로 디렉토리로 자동 구성 하는 역할
    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    private MockMvc mockMvc;

    @MockBean
    private NoticeService noticeService;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .apply(documentationConfiguration(this.restDocumentation))
                .build();
    }

    @DisplayName("학사 카테고리의 공지 가져오기")
    @Test
    public void getNoticesTest() throws Exception {

        String type = "bch";
        int offset = 0;
        int max = 10;

        String articleId = "5cw2e1";
        String postedDate = "20211016";
        String subject = "[학사] 2021년 학사 관련 공지";
        String categoryName = "bachelor";

        List<NoticeDTO> noticeDTOList = new LinkedList<>();
        noticeDTOList.add(NoticeDTO.builder()
                .articleId(articleId)
                .postedDate(postedDate)
                .subject(subject)
                .categoryName(categoryName)
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
                .andExpect(jsonPath("baseUrl").exists())
                .andExpect(jsonPath("noticeList").exists())
                .andDo(document("notice",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestParameters(
                                        parameterWithName("type").description("공지 카테고리 키워드"),
                                        parameterWithName("offset").description("가져올 공지의 시작 인덱스"),
                                        parameterWithName("max").description("가져올 공지 최대 개수")
                                ),
                                responseFields(
                                        fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                        fieldWithPath("resultMsg").type(JsonFieldType.STRING).description("결과 메세지"),
                                        fieldWithPath("resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                        fieldWithPath("baseUrl").type(JsonFieldType.STRING).description("공지 확인할 수 있는 url의 공통 부분"),
                                        fieldWithPath("noticeList[].articleId").type(JsonFieldType.STRING).description("공지 ID"),
                                        fieldWithPath("noticeList[].postedDate").type(JsonFieldType.STRING).description("공지 게시일"),
                                        fieldWithPath("noticeList[].subject").type(JsonFieldType.STRING).description("공지 제목"),
                                        fieldWithPath("noticeList[].category").type(JsonFieldType.STRING).description("공지 카테고리명")
                                ))

                );
    }
}
