package com.kustacks.kuring.service;

public interface TriggerService {
    boolean checkAuth(String token);
    void triggerNoticeWorker(String type);
    void triggerStaffWorker(String type);
    void triggerUserWorker(String type);
}
