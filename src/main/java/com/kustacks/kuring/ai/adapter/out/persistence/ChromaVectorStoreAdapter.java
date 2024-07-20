package com.kustacks.kuring.ai.adapter.out.persistence;

import com.kustacks.kuring.ai.application.port.out.CommandVectorStorePort;
import com.kustacks.kuring.ai.application.port.out.QueryVectorStorePort;
import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.worker.parser.notice.PageTextDto;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.ChromaVectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("prod | local")
@RequiredArgsConstructor
public class ChromaVectorStoreAdapter implements QueryVectorStorePort, CommandVectorStorePort {

    private static final int TOP_K = 1;

    private final ChromaVectorStore chromaVectorStore;

    @Override
    public List<String> findSimilarityContents(String question) {
        return chromaVectorStore.similaritySearch(
                        SearchRequest.query(question).withTopK(TOP_K)
                ).stream()
                .map(Document::getContent)
                .toList();
    }

    @Override
    public void embedding(List<PageTextDto> extractTextResults, CategoryName categoryName) {
        TokenTextSplitter textSplitter = new TokenTextSplitter();

        for (PageTextDto textResult : extractTextResults) {
            if (textResult.text().isBlank()) continue;

            List<Document> documents = createDocuments(categoryName, textResult);
            List<Document> splitDocuments = textSplitter.apply(documents);
            chromaVectorStore.accept(splitDocuments);
        }
    }

    private List<Document> createDocuments(CategoryName categoryName, PageTextDto textResult) {
        Resource resource = new ByteArrayResource(textResult.text().getBytes()) {
            @Override
            public String getFilename() {
                return textResult.title();
            }
        };

        TextReader textReader = new TextReader(resource);
        textReader.getCustomMetadata().put("articleId", textResult.articleId());
        textReader.getCustomMetadata().put("category", categoryName.getName());
        return textReader.get();
    }
}
