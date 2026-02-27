package com.kustacks.kuring.storage.application.port.in.dto;

import java.io.InputStream;
import java.util.List;

public record UploadFileCommand(
        List<UploadFile> files
) {
    public record UploadFile(
            String key,
            InputStream inputStream, // 아래 내용들을.
            String contentType,
            Long size
    ) {
    }
}
