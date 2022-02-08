package com.kustacks.kuring.kuapi.api.notice;

import com.kustacks.kuring.kuapi.api.APIClient;

public interface NoticeAPIClient<T, K> extends APIClient<T, K> {
    int SCRAP_TIMEOUT = 600000;
}
