package com.kustacks.kuring.ai.adapter.out.persistence;

import com.kustacks.kuring.ai.application.port.out.QueryVectorStorePort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@Profile("prod")
@RequiredArgsConstructor
public class QueryVectorStoreAdapter implements QueryVectorStorePort {

    @Override
    public List<String> findSimilarityContents(String question) {
        return Collections.emptyList();
    }
}
