package com.kustacks.kuring.worker.scrap.client.auth;

public interface KuisAuthManager {
    String getSessionId();
    void forceRenewing();
}
