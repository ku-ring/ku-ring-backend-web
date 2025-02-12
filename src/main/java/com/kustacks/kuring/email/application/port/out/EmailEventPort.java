package com.kustacks.kuring.email.application.port.out;

public interface EmailEventPort {
    void sendEmailEvent(String from, String to, String subject, String body);
}
