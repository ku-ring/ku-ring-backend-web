package com.kustacks.kuring.ai.adapter.in.event;

import com.kustacks.kuring.ai.adapter.in.event.dto.DataEmbeddingEvent;
import com.kustacks.kuring.ai.application.port.in.RAGCommandUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataEmbeddingEventListener {

    private final RAGCommandUseCase ragCommandUseCase;

    @Async
    @EventListener
    public void dataEmbeddingEvent(
            DataEmbeddingEvent event
    ) {
        ragCommandUseCase.dataEmbedding(
                event.fileName(),
                event.extension(),
                event.contentType(),
                event.resource()
        );
    }
}
