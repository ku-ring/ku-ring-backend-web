package com.kustacks.kuring.category.business.event;

import com.kustacks.kuring.user.domain.UserCategory;
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
