package com.kustacks.kuring.ai.application.port.out;

public interface RAGEventPort {
    void userDecreaseQuestionCountEvent(String userId);

    void rootUserDecreaseQuestionCountEvent(String userId, String email);
}
