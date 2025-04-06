package com.kustacks.kuring.ai.application.port.out;

public interface RAGEventPort {
    void askedQuestion(String userId, String email);
}
