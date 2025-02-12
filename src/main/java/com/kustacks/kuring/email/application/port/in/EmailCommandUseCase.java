package com.kustacks.kuring.email.application.port.in;

import com.kustacks.kuring.email.application.port.in.dto.EmailSendCommand;

public interface EmailCommandUseCase {
    void sendVerificationEmail(String email);
    void sendEmail(EmailSendCommand command);
}
