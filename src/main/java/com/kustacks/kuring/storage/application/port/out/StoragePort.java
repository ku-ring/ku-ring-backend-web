package com.kustacks.kuring.storage.application.port.out;

import java.io.*;

public interface StoragePort {
    void upload(InputStream inputStream, String key, String contentType) throws IOException;
    String getPresignedUrl(String key);
    void delete(String key);
}
