package com.kustacks.kuring.worker.api.notice;

public interface KuisAuthManager {
    String getSessionId();
    void forceRenewing();
}
