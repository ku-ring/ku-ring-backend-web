package com.kustacks.kuring.email.application.service;

import com.kustacks.kuring.email.application.port.out.EmailClientPort;
import com.kustacks.kuring.email.application.port.out.TemplateEnginePort;
import com.kustacks.kuring.email.application.port.out.VerificationCodeCommandPort;
import com.kustacks.kuring.email.domain.VerificationCode;
import com.kustacks.kuring.support.TestFileLoader;
import com.kustacks.kuring.user.application.port.out.UserQueryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
class EmailCommandUseCaseTest {

    @InjectMocks
    private EmailCommandService emailCommandService;

    @Mock
    private VerificationCodeCommandPort verificationCodeCommandPort;

    @Mock
    private EmailClientPort emailClientPort;

    @Mock
    private TemplateEnginePort templateEnginePort;

    @Mock
    private UserQueryPort userQueryPort;

    @DisplayName("인증코드 이메일 전송 성공 테스트")
    @Test
    void send_verification_code_email_success() throws IOException {
        //given
        String email = "client@konkuk.ac.kr";
        String templatePage = TestFileLoader.loadHtmlFile("src/test/resources/email/thymeleaf_engine_process_result.html");

        Mockito.when(templateEnginePort.process(Mockito.anyString(), Mockito.anyMap())).thenReturn(templatePage);
        Mockito.when(userQueryPort.existByEmail(Mockito.anyString())).thenReturn(false);

        //when
        emailCommandService.sendVerificationEmail(email);

        //then
        Mockito.verify(templateEnginePort, Mockito.times(1))
                .process(Mockito.anyString(), Mockito.anyMap());

        Mockito.verify(emailClientPort, Mockito.times(1))
                .sendEmailAsync(Mockito.any());

        Mockito.verify(verificationCodeCommandPort, Mockito.times(1))
                .saveVerificationCode(Mockito.any(VerificationCode.class));
    }
}
