package com.kustacks.kuring.storage.application.port.in;

import com.kustacks.kuring.storage.application.port.in.dto.UploadFileCommand;

public interface StorageUploadUseCase {
    void uploadAll(UploadFileCommand command);
}
