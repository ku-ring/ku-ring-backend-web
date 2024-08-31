package com.kustacks.kuring.ai.application.port.out;

import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.worker.parser.notice.PageTextDto;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

public interface CommandVectorStorePort {

    void embedding(List<PageTextDto> extractTextResults, CategoryName categoryName);

    void embeddingSingleTextFile(String originName, Resource resource) throws IOException;
}
