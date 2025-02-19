package com.kustacks.kuring.email.application.service;

import com.kustacks.kuring.email.application.port.out.VerificationCodeQueryPort;
import com.kustacks.kuring.email.application.service.exception.EmailBusinessException;
import com.kustacks.kuring.email.domain.VerificationCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

class EmailVerifyUseCaseTest {

    private EmailVerifyService emailVerifyUseCase;

    @Mock
    private VerificationCodeQueryPort verificationCodeQueryPort = Mockito.mock(VerificationCodeQueryPort.class);

    public EmailVerifyUseCaseTest() {
        emailVerifyUseCase = new EmailVerifyService(verificationCodeQueryPort);
    }

    @DisplayName("이메일 코드 검증 성공")
    @Test
    void success_verify_code(){
        //given
        String email = "test@test.com";
        String code = "123456";
        Mockito.when(verificationCodeQueryPort.findCodesByEmail(email)).thenReturn(List.of(new VerificationCode(email, code)));

        //when, then
        Assertions.assertThatCode(
                () -> emailVerifyUseCase.verifyLatestCode(email, code)
        ).doesNotThrowAnyException();
    }

    @DisplayName("이메일 코드 검증 실패")
    @Test
    void fail_verify_code() {
        //given
        String email = "test@test.com";
        String wrongCode = "654321";
        Mockito.when(verificationCodeQueryPort.findCodesByEmail(email)).thenReturn(new ArrayList<>());

        //when, then
        Assertions.assertThatThrownBy(
                        () -> emailVerifyUseCase.verifyLatestCode(email, wrongCode))
                .isInstanceOf(EmailBusinessException.class);
    }
}