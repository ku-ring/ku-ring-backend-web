package com.kustacks.kuring.ai.adapter.in.web;

import com.kustacks.kuring.ai.application.port.in.RAGQueryUseCase;
import com.kustacks.kuring.common.annotation.RestWebAdapter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@RestWebAdapter(path = "/api/v2/ai/messages")
public class RAGQueryApiV2 {

    private static final String USER_TOKEN_HEADER_KEY = "User-Token";

    private final RAGQueryUseCase ragQueryUseCase;

    @Operation(summary = "사용자 AI에 질문요청", description = "사용자가 궁금한 학교 정보를 AI에게 질문합니다.")
    @SecurityRequirement(name = "User-Token")
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> askAIQuery(
            @RequestParam("question") String question,
            @RequestHeader(USER_TOKEN_HEADER_KEY) String id
    ) {
        return ragQueryUseCase.askAiModel(question, id);
    }
}
