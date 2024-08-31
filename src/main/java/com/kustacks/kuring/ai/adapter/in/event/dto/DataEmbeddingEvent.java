package com.kustacks.kuring.ai.adapter.in.event.dto;

import org.springframework.core.io.Resource;

public record DataEmbeddingEvent(
        String fileName,
        String extension,
        String contentType,
        Resource resource
) {
}
