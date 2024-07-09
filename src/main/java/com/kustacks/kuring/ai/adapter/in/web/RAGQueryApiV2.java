package com.kustacks.kuring.ai.adapter.in.web;

import com.kustacks.kuring.ai.adapter.in.web.dto.UserQuestionRequest;
import com.kustacks.kuring.ai.application.port.in.RAGQueryUseCase;
import com.kustacks.kuring.common.annotation.RestWebAdapter;
import com.kustacks.kuring.common.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.NOTICE_SEARCH_SUCCESS;

@RequiredArgsConstructor
@RestWebAdapter(path = "/api/v2/ai/messages")
public class RAGQueryApiV2 {

    private static final String USER_TOKEN_HEADER_KEY = "User-Token";

    private final RAGQueryUseCase ragQueryUseCase;

    @Operation(summary = "사용자 AI에 질문요청", description = "사용자가 궁금한 학교 정보를 AI에게 질문합니다.")
    @SecurityRequirement(name = "User-Token")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> askAIQuery(
            @RequestBody UserQuestionRequest questionRequest,
            @RequestHeader(USER_TOKEN_HEADER_KEY) String id
    ) {
        String answer = ragQueryUseCase.askAiModel(questionRequest.question(), id);
        return ResponseEntity.ok().body(new BaseResponse<>(NOTICE_SEARCH_SUCCESS, answer));
    }
}
