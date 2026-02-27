package com.kustacks.kuring.storage.adapter.in.event.dto;

import com.kustacks.kuring.storage.application.port.in.dto.UploadFileCommand;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public record ClubCreateEvent(
        Long clubId,
        ClubCreateImage logoImage,
        ClubCreateImage posterImage
) {
    public UploadFileCommand toCommand() {
        return new UploadFileCommand(
                Stream.of(logoImage, posterImage)
                        .filter(Objects::nonNull)
                        .map(ClubCreateImage::toUploadFile)
                        .toList()
        );
    }

    public record ClubCreateImage(
            String pathAndName,
            InputStream inputstream,
            String contentType,
            Long size
    ) {
        public ClubCreateImage(String filePath, MultipartFile file) throws IOException {
            this(filePath, file.getInputStream(), file.getContentType(), file.getSize());
        }

        public UploadFileCommand.UploadFile toUploadFile() {
            return new UploadFileCommand.UploadFile(pathAndName, inputstream, contentType, size);
        }
    }
}
