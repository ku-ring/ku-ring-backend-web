package com.kustacks.kuring.email.adapter.in.web;

import com.kustacks.kuring.common.annotation.RestWebAdapter;
import com.kustacks.kuring.common.dto.BaseResponse;
import com.kustacks.kuring.common.dto.ResponseCodeAndMessages;
import com.kustacks.kuring.email.adapter.in.web.dto.EmailVerificationRequest;
import com.kustacks.kuring.email.application.port.in.EmailCommandUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Email-Query", description = "Email Send")
@RestWebAdapter(path = "/api/v2/email")
@RequiredArgsConstructor
public class EmailCommandApiV2 {
    private final EmailCommandUseCase emailCommandUseCase;

    @Operation(summary = "이메일로 인증번호 보내기", description = "사용자 회원가입 시 이메일로 인증번호를 보냅니다.")
    @PostMapping("/send-verification-code")
    public ResponseEntity<BaseResponse<Void>> sendVerificationCode(@RequestBody EmailVerificationRequest request) {
        emailCommandUseCase.sendVerificationEmail(request.email());
        return ResponseEntity.ok().body(new BaseResponse<>(ResponseCodeAndMessages.EMAIL_SEND_SUCCESS, null));
    }
}
