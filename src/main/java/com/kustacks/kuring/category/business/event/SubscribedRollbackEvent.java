package com.kustacks.kuring.category.business.event;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SubscribedRollbackEvent {

    private static final String NEW_CATEGORY_FLAG = "new";
    private static final String REMOVE_CATEGORY_FLAG = "remove";

    private final Map<String, List<String>> transactionHistory;
    private final String token;

    public SubscribedRollbackEvent(String token) {
        this.token = token;
        this.transactionHistory = new HashMap<>();
        historyInit();
    }

    public void addNewCategoryName(String categoryName) {
        this.transactionHistory.get(NEW_CATEGORY_FLAG).add(categoryName);
    }

    public void deleteNewCategoryName(String categoryName) {
        this.transactionHistory.get(REMOVE_CATEGORY_FLAG).add(categoryName);
    }

    private void historyInit() {
        transactionHistory.put(NEW_CATEGORY_FLAG, new LinkedList<>());
        transactionHistory.put(REMOVE_CATEGORY_FLAG, new LinkedList<>());
    }

    public List<String> getNewUserCategoryNames() {
        return this.transactionHistory.get(NEW_CATEGORY_FLAG);
    }

    public List<String> getRemovedUserCategoryNames() {
        return this.transactionHistory.get(REMOVE_CATEGORY_FLAG);
    }

    public String getToken() {
        return this.token;
    }
}
