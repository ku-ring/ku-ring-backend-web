package com.kustacks.kuring.ai.application.port.out;

import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.worker.parser.notice.PageTextDto;

import java.util.List;

public interface CommandVectorStorePort {
    void embedding(List<PageTextDto> extractTextResults, CategoryName categoryName);
}
