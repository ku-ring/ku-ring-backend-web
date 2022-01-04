package com.kustacks.kuring.kuapi.notice;

public interface KuisAuthManager {
    String getSessionId();
    void forceRenewing();
}