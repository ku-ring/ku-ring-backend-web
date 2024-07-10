package com.kustacks.kuring.ai.application.port.out;

import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.publisher.Flux;

public interface QueryAiModelPort {
    Flux<String> call(Prompt prompt);
}
