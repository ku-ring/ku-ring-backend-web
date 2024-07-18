package com.kustacks.kuring.ai.adapter.out.persistence;

import com.kustacks.kuring.ai.application.port.out.CommandVectorStorePort;
import com.kustacks.kuring.ai.application.port.out.QueryVectorStorePort;
import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.worker.parser.notice.PageTextDto;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.vectorstore.ChromaVectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("prod | local")
@RequiredArgsConstructor
public class ChromaVectorStoreAdapter implements QueryVectorStorePort, CommandVectorStorePort {

    private static final double SIMILARITY_THRESHOLD = 0.80;
    private static final int TOP_K = 1;

    private final ChromaVectorStore chromaVectorStore;
    private final TextSplitter TokenTextSplitter;

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

    @Override
    public void embedding(List<PageTextDto> extractTextResults, CategoryName categoryName) {
        for (PageTextDto textResult : extractTextResults) {
            List<Document> documents = createDocuments(categoryName, textResult);
            List<Document> splitDocuments = TokenTextSplitter.apply(documents);
            chromaVectorStore.accept(splitDocuments);
        }
    }

    private List<Document> createDocuments(CategoryName categoryName, PageTextDto textResult) {
        TextReader textReader = new TextReader(textResult.text());
        textReader.getCustomMetadata().put("filename", textResult.title());
        textReader.getCustomMetadata().put("articleId", textResult.articleId());
        textReader.getCustomMetadata().put("category", categoryName.getName());
        return textReader.get();
    }
}
