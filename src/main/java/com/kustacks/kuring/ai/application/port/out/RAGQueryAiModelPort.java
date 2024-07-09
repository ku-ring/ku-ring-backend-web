package com.kustacks.kuring.ai.application.port.out;

import org.springframework.ai.chat.prompt.Prompt;

public interface RAGQueryAiModelPort {
    String call(Prompt prompt);
}
