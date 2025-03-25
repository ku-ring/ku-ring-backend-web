package com.kustacks.kuring.ai.adapter.in.web;

import com.kustacks.kuring.ai.application.port.in.RAGQueryUseCase;
import com.kustacks.kuring.auth.authentication.AuthorizationExtractor;
import com.kustacks.kuring.auth.authentication.AuthorizationType;
import com.kustacks.kuring.auth.token.JwtTokenProvider;
import com.kustacks.kuring.common.annotation.RestWebAdapter;
import com.kustacks.kuring.common.exception.InvalidStateException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;

import static com.kustacks.kuring.auth.authentication.AuthorizationExtractor.extractAuthorizationValue;

@Tag(name = "AI-Query", description = "AI Assistant")
@RequiredArgsConstructor
@RestWebAdapter(path = "/api/v2/ai/messages")
public class RAGQueryApiV2 {

    private static final String FCM_TOKEN_HEADER_KEY = "User-Token";
    private static final String JWT_TOKEN_HEADER_KEY = "JWT";

    private final JwtTokenProvider jwtTokenProvider;
    private final RAGQueryUseCase ragQueryUseCase;

    @Operation(summary = "사용자 AI에 질문요청", description = "사용자가 궁금한 학교 정보를 AI에게 질문합니다.")
    @SecurityRequirement(name = FCM_TOKEN_HEADER_KEY)
    @SecurityRequirement(name = JWT_TOKEN_HEADER_KEY)
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> askAIQuery(
            @Parameter(description = "사용자 질문") @RequestParam("question") String question,
            @RequestHeader(FCM_TOKEN_HEADER_KEY) String id,
            @RequestHeader(value = AuthorizationExtractor.AUTHORIZATION, required = false) String bearerToken
    ) {
        if (bearerToken == null) {
            return ragQueryUseCase.askAiModel(question, id);
        } else {
            String accessToken = extractAuthorizationValue(bearerToken, AuthorizationType.BEARER);
            String email = validateJwtAndGetEmail(accessToken);

            return ragQueryUseCase.askAiModelWithEmail(question, id, email);
        }
    }

    private String validateJwtAndGetEmail(String jwtToken) {
        if (!jwtTokenProvider.validateToken(jwtToken)) {
            throw new InvalidStateException(ErrorCode.JWT_INVALID_TOKEN);
        }
        return jwtTokenProvider.getPrincipal(jwtToken);
    }
}
