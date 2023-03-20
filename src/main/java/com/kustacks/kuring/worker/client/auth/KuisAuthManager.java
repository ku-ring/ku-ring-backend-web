package com.kustacks.kuring.worker.client.auth;

public interface KuisAuthManager {
    String getSessionId();
    void forceRenewing();
}
