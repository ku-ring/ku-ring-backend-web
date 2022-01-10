package com.kustacks.kuring.kuapi.api.notice;

public interface KuisAuthManager {
    String getSessionId();
    void forceRenewing();
}