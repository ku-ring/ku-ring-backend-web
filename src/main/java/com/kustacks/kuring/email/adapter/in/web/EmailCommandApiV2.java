package com.kustacks.kuring.email.adapter.in.web;

import com.kustacks.kuring.auth.authentication.AuthorizationExtractor;
import com.kustacks.kuring.auth.authentication.AuthorizationType;
import com.kustacks.kuring.auth.token.JwtTokenProvider;
import com.kustacks.kuring.common.annotation.RestWebAdapter;
import com.kustacks.kuring.common.dto.BaseResponse;
import com.kustacks.kuring.common.dto.ResponseCodeAndMessages;
import com.kustacks.kuring.common.exception.InvalidStateException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.email.adapter.in.web.dto.EmailVerificationRequest;
import com.kustacks.kuring.email.application.port.in.EmailCommandUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import static com.kustacks.kuring.auth.authentication.AuthorizationExtractor.extractAuthorizationValue;

@Slf4j
@Tag(name = "Email-Query", description = "Email Send")
@RestWebAdapter(path = "/api/v2/verification-code")
@RequiredArgsConstructor
public class EmailCommandApiV2 {

    private static final String JWT_TOKEN_HEADER_KEY = "JWT";

    private final EmailCommandUseCase emailCommandUseCase;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "이메일로 인증번호 보내기(회원가입 시)", description = "사용자 회원가입 시 이메일로 인증번호를 보냅니다.")
    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<Void>> sendVerificationCode(@RequestBody EmailVerificationRequest request) {
        emailCommandUseCase.sendSignupVerificationEmail(request.email());
        return ResponseEntity.ok().body(new BaseResponse<>(ResponseCodeAndMessages.EMAIL_SEND_SUCCESS, null));
    }

    @Operation(summary = "이메일로 인증번호 보내기(비밀번호 초기화 시)", description = "사용자 비밀번호 초기화 시 이메일로 인증번호를 보냅니다.")
    @SecurityRequirement(name = JWT_TOKEN_HEADER_KEY)
    @PostMapping("/password-reset")
    public ResponseEntity<BaseResponse<Void>> sendPasswordResetVerificationCode(
            @RequestHeader(AuthorizationExtractor.AUTHORIZATION) String bearerToken
    ) {
        String jwtToken = extractAuthorizationValue(bearerToken, AuthorizationType.BEARER);
        String email = validateJwtAndGetEmail(jwtToken);

        emailCommandUseCase.sendPasswordResetVerificationEmail(email);
        return ResponseEntity.ok().body(new BaseResponse<>(ResponseCodeAndMessages.EMAIL_SEND_SUCCESS, null));
    }

    private String validateJwtAndGetEmail(String jwtToken) {
        if (!jwtTokenProvider.validateToken(jwtToken)) {
            throw new InvalidStateException(ErrorCode.JWT_INVALID_TOKEN);
        }
        return jwtTokenProvider.getPrincipal(jwtToken);
    }
}
