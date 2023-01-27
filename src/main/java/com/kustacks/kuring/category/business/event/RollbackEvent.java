package com.kustacks.kuring.category.business.event;

import lombok.Getter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
public class RollbackEvent {

    private final Map<String, List<String>> transactionHistory;
    private final String token;

    public RollbackEvent(String token) {
        this.token = token;
        this.transactionHistory = new HashMap<>();
        HistoryInit();
    }

    public void addNewCategoryName(String categoryName) {
        this.transactionHistory.get("new").add(categoryName);
    }

    public void deleteNewCategoryName(String categoryName) {
        this.transactionHistory.get("remove").add(categoryName);
    }

    private void HistoryInit() {
        transactionHistory.put("new", new LinkedList<>());
        transactionHistory.put("remove", new LinkedList<>());
    }
}
