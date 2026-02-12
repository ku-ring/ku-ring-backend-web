package com.kustacks.kuring.storage.application.port.out;

import java.io.*;
import java.net.URL;

public interface StoragePort {
    void upload(InputStream inputStream, String key, String contentType) throws IOException;
    URL getPresignedUrl(String key);
    void delete(String key);
}
