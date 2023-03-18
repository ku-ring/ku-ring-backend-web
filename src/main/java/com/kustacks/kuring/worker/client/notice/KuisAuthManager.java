package com.kustacks.kuring.worker.client.notice;

public interface KuisAuthManager {
    String getSessionId();
    void forceRenewing();
}
