package com.kustacks.kuring.admin.application.port.out;

import org.springframework.core.io.Resource;

public interface AiEventPort {

    void sendDataEmbeddingEvent(String originName, String extension, String contentType, Resource resource);
}
