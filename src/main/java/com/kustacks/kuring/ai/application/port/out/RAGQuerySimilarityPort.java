package com.kustacks.kuring.ai.application.port.out;

import java.util.List;

public interface RAGQuerySimilarityPort {
    List<String> findSimilarityContents(String question);
}
