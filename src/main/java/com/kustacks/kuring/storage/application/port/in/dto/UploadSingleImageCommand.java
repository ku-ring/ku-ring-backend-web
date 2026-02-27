package com.kustacks.kuring.storage.application.port.in.dto;

public record UploadSingleImageCommand(
        String path,
        String fileName,
        byte[] fileBytes, // 아래 내용들을.
        String contentType,
        String originalFileName
) {
}
