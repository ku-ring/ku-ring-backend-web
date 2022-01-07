package com.kustacks.kuring.kuapi.notice;

import com.kustacks.kuring.kuapi.api.notice.NoticeAPIClient;

public interface KuisAuthManager {
    String getSessionId();
    void forceRenewing();
    void observe(NoticeAPIClient noticeAPIClient);
}