package com.kustacks.kuring.ai.application.port.in;

import reactor.core.publisher.Flux;

public interface RAGQueryUseCase {
    Flux<String> askAiModel(String question, String id, String email);
}
