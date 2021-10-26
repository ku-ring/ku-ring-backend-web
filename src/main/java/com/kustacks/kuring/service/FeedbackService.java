package com.kustacks.kuring.service;

import com.kustacks.kuring.domain.user.User;

public interface FeedbackService {
    void insertFeedback(String token, String content);
}
