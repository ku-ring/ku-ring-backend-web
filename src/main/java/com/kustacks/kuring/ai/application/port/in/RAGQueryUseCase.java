package com.kustacks.kuring.ai.application.port.in;

public interface RAGQueryUseCase {
    String askAiModel(String question, String id);
}
