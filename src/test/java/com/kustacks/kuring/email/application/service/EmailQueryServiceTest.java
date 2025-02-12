package com.kustacks.kuring.email.application.service;

import com.kustacks.kuring.email.application.port.out.VerificationCodeQueryPort;
import com.kustacks.kuring.email.application.service.exception.EmailBusinessException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

class EmailQueryServiceTest {

    private EmailQueryService emailQueryService;

    @MockBean
    private VerificationCodeQueryPort verificationCodeQueryPort = Mockito.mock(VerificationCodeQueryPort.class);

    public EmailQueryServiceTest() {
        emailQueryService = new EmailQueryService(verificationCodeQueryPort);
    }

    @DisplayName("이메일 코드 검증 성공")
    @Test
    public void success_verify_code() throws Exception {
        //given
        String email = "test@test.com";
        String code = "123456";

        //when
        Mockito.when(verificationCodeQueryPort.findCodeByEmail(email)).thenReturn(code);

        //then
        Assertions.assertThatCode(
                () -> emailQueryService.verifyCode(email, code)
        ).doesNotThrowAnyException();
    }

    @DisplayName("이메일 코드 검증 실패")
    @Test
    public void fail_verify_code() throws Exception {
        //given
        String email = "test@test.com";
        String code = "123456";

        String wrongCode = "654321";

        //when
        Mockito.when(verificationCodeQueryPort.findCodeByEmail(email)).thenReturn(wrongCode);

        //then
        Assertions.assertThatThrownBy(
                        () -> emailQueryService.verifyCode(email, code))
                .isInstanceOf(EmailBusinessException.class);
    }
}