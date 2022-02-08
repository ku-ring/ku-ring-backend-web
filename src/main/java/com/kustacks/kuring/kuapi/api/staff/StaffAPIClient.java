package com.kustacks.kuring.kuapi.api.staff;

import com.kustacks.kuring.kuapi.api.APIClient;

public interface StaffAPIClient<T, K> extends APIClient<T, K> {
    int SCRAP_TIMEOUT = 20000;
}
