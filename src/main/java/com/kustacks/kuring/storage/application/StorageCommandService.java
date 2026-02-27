package com.kustacks.kuring.storage.application;

import com.kustacks.kuring.storage.application.port.in.StorageUploadUseCase;
import com.kustacks.kuring.storage.application.port.in.dto.UploadFileCommand;
import com.kustacks.kuring.storage.application.port.out.StoragePort;
import com.kustacks.kuring.storage.exception.CloudStorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageCommandService implements StorageUploadUseCase {

    private final StoragePort storagePort;

    @Override
    public void uploadAll(UploadFileCommand command) {
        command.files().stream()
                .filter(Objects::nonNull)
                .forEach(this::upload);
    }

    private void upload(UploadFileCommand.UploadFile file) {
        try (InputStream inputStream = file.inputStream()) {
            storagePort.upload(
                    inputStream,
                    file.key(),
                    file.contentType(),
                    file.size()
            );
            log.info("파일 업로드 완료, file={}", file.key());
        } catch (IOException e) {
            log.error("{} : file stream close/read fail", file.key());
        } catch (CloudStorageException e) {
            log.error("{} : {}", file.key(), e.getErrorCode().getMessage());
        }
    }
}
