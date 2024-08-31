package com.kustacks.kuring.alert.application.port.in.dto;

import org.springframework.web.multipart.MultipartFile;

public record DataEmbeddingCommand(
        MultipartFile file
) {
}
