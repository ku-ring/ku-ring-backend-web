package com.kustacks.kuring.ai.adapter.out;

import com.kustacks.kuring.ai.application.port.out.RAGQuerySimilarityPort;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("prod | local | test")
@RequiredArgsConstructor
public class RAGQueryVectorStoreAdapter implements RAGQuerySimilarityPort {

    private final VectorStore simpleVectorStore;

    @Override
    public List<String> findSimilarityContents(String question) {
        List<Document> similarDocuments = simpleVectorStore.similaritySearch(
                SearchRequest.query(question).withTopK(1)
        );

        return similarDocuments.stream()
                .map(Document::getContent)
                .toList();
    }
}
