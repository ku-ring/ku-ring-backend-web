package com.kustacks.kuring.ai.application.service;

import com.kustacks.kuring.ai.application.port.in.RAGCommandUseCase;
import com.kustacks.kuring.ai.application.port.out.CommandVectorStorePort;
import com.kustacks.kuring.common.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class RAGCommandService implements RAGCommandUseCase {

    private final CommandVectorStorePort commandVectorStorePort;
    private static final String EXTENSION_PDF = "pdf";
    private static final String EXTENSION_TXT = "txt";

    @Override
    public void dataEmbedding(String originName, String extension, String contentType, Resource resource) {
        try {
            if (extension.equals(EXTENSION_PDF)) {
                // TODO: pdf embedding
            } else if (extension.equals(EXTENSION_TXT)) {
                commandVectorStorePort.embeddingSingleTextFile(originName, resource);
            } else {
                log.warn("not supported file type : {}", extension);
            }
        } catch (IOException e) {
            log.warn("file embedding fail : {}", originName);
        }
    }
}
