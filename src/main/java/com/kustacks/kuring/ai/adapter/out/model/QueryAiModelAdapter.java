package com.kustacks.kuring.ai.adapter.out.model;

import com.kustacks.kuring.ai.application.port.out.QueryAiModelPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Slf4j
@Component
@Profile("prod")
@RequiredArgsConstructor
public class QueryAiModelAdapter implements QueryAiModelPort {

    private final OpenAiChatModel openAiChatModel;

    @Override
    public Flux<String> call(Prompt prompt) {
        return openAiChatModel.stream(prompt)
                .filter(chatResponse -> chatResponse.getResult().getOutput().getContent() != null)
                .flatMap(chatResponse -> Flux.just(chatResponse.getResult().getOutput().getContent()))
                .doOnError(throwable -> log.error("[RAGQueryAiModelAdapter] {}", throwable.getMessage()));
    }
}
