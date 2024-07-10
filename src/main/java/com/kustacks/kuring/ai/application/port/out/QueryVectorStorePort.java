package com.kustacks.kuring.ai.application.port.out;

import java.util.List;

public interface QueryVectorStorePort {
    List<String> findSimilarityContents(String question);
}
