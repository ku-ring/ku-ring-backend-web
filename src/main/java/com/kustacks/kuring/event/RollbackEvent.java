package com.kustacks.kuring.event;

import com.kustacks.kuring.domain.user_category.UserCategory;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class RollbackEvent {

    private final Map<String, List<UserCategory>> transactionHistory;
    private final String token;

    public RollbackEvent(String token, Map<String, List<UserCategory>> transactionHistory) {
        this.transactionHistory = transactionHistory;
        this.token = token;
    }
}
