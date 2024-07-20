package com.kustacks.kuring.ai.adapter.out.persistence;

import com.kustacks.kuring.ai.application.port.out.CommandVectorStorePort;
import com.kustacks.kuring.ai.application.port.out.QueryVectorStorePort;
import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.worker.parser.notice.PageTextDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Profile("dev | test")
@Component
@RequiredArgsConstructor
public class InMemoryVectorStoreAdapter implements QueryVectorStorePort, CommandVectorStorePort {

    @Override
    public List<String> findSimilarityContents(String question) {
        HashMap<String, Object> metadata = createMetaData();

        Document document = createDocument(metadata);

        return Stream.of(document)
                .map(Document::getContent)
                .toList();
    }

    @Override
    public void embedding(List<PageTextDto> extractTextResults, CategoryName categoryName) {
        log.info("[InMemoryQueryVectorStoreAdapter] embedding {}", categoryName);
    }

    private Document createDocument(HashMap<String, Object> metadata) {
        return new Document(
                "a5a7414f-f676-409b-9f2e-1042f9846c97",
                """
                         ● 등록금 전액 완납 또는 분할납부 1차분을 정해진 기간에 미납할 경우 분할납부 신청은 자동 취소되며,
                         미납 등록금은 이후 추가 등록기간에 전액 납부해야 함.\n
                         """,
                metadata);
    }

    private HashMap<String, Object> createMetaData() {
        HashMap<String, Object> metadata = new HashMap<>();
        metadata.put("charset", "UTF-8");
        metadata.put("filename", "ku-uni-register.txt");
        metadata.put("source", "ku-uni-register.txt");
        return metadata;
    }
}
