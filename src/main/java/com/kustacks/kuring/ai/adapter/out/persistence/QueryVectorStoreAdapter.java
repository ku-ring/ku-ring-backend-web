package com.kustacks.kuring.ai.adapter.out.persistence;

import com.kustacks.kuring.ai.application.port.out.QueryVectorStorePort;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.ChromaVectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("prod | local")
@RequiredArgsConstructor
public class QueryVectorStoreAdapter implements QueryVectorStorePort {

    private static final double SIMILARITY_THRESHOLD = 0.80;
    private static final int TOP_K = 1;

    private final ChromaVectorStore chromaVectorStore;

    @Override
    public List<String> findSimilarityContents(String question) {
        return chromaVectorStore.similaritySearch(
                        SearchRequest.query(question)
                                .withTopK(TOP_K)
                                .withSimilarityThreshold(SIMILARITY_THRESHOLD)
                ).stream()
                .map(Document::getContent)
                .toList();
    }
}
