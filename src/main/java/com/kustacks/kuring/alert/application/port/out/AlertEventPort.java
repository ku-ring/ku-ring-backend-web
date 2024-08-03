package com.kustacks.kuring.alert.application.port.out;

public interface AlertEventPort {

    void sendAlertEvent(String title, String content);
}
