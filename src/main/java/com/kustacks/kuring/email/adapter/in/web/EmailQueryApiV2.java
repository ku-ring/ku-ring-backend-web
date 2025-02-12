package com.kustacks.kuring.email.adapter.in.web;

import com.kustacks.kuring.common.annotation.RestWebAdapter;
import com.kustacks.kuring.common.dto.BaseResponse;
import com.kustacks.kuring.common.dto.ResponseCodeAndMessages;
import com.kustacks.kuring.email.adapter.in.web.dto.EmailVerifyCodeRequest;
import com.kustacks.kuring.email.application.port.in.EmailQueryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Email-Query", description = "Email Send")
@RestWebAdapter(path = "/api/v2/email")
@RequiredArgsConstructor
public class EmailQueryApiV2 {
    private final EmailQueryUseCase emailQueryUseCase;

    @Operation(summary = "인증번호 인증하기", description = "사용자의 이메일로 보낸 인증번호를 인증합니다.")
    @PostMapping("/verify-code")
    public ResponseEntity<BaseResponse<Void>> sendVerificationCode(@RequestBody EmailVerifyCodeRequest request) {
        emailQueryUseCase.verifyCode(request.email(), request.code());
        return ResponseEntity.ok().body(new BaseResponse<>(ResponseCodeAndMessages.EMAIL_CODE_VERIFY_SUCCESS, null));
    }
}
