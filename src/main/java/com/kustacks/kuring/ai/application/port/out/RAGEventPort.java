package com.kustacks.kuring.ai.application.port.out;

public interface RAGEventPort {
    void userDecreaseQuestionCountEvent(String userId, String email);
}
