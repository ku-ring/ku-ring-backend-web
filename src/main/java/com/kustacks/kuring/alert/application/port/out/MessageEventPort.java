package com.kustacks.kuring.alert.application.port.out;

public interface MessageEventPort {

    void sendMessageEvent(String title, String content);
}
