package com.kustacks.kuring.admin.adapter.out.event;

import com.kustacks.kuring.admin.application.port.out.AiEventPort;
import com.kustacks.kuring.ai.adapter.in.event.dto.DataEmbeddingEvent;
import com.kustacks.kuring.common.domain.Events;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminAiEventAdapter implements AiEventPort {

    @Override
    public void sendDataEmbeddingEvent(String originName, String extension, String contentType, Resource resource) {
        Events.raise(new DataEmbeddingEvent(originName, extension, contentType, resource));
    }
}
