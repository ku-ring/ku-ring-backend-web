package com.kustacks.kuring.storage.application.port.out;

import java.io.InputStream;

public interface StoragePort {
    void upload(InputStream inputStream, String key, String contentType);
    String getPresignedUrl(String key);
    void delete(String key);
}
