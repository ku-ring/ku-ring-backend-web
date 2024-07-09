package com.kustacks.kuring.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Configuration
public class RAGConfiguration {

    @Value("classpath:/ai/docs/ku-uni-register.txt")
    private Resource kuUniRegisterInfo;

    @Value("vectorstore.json")
    private String vectorStoreName;

    @Bean
    public SimpleVectorStore simpleVectorStore(EmbeddingModel embeddingModel) {
        SimpleVectorStore simpleVectorStore = new SimpleVectorStore(embeddingModel);
        File vectorStoreFile = getVectorStoreFile();

        if (vectorStoreFile.exists()) {
            log.info("Vector store file exists");
            simpleVectorStore.load(vectorStoreFile);
            return simpleVectorStore;
        }

        log.info("Vector store file does not exist. Loading vector store from resource: {}",
                kuUniRegisterInfo.getFilename());

        List<Document> splitDocument = getSplitDocument();
        simpleVectorStore.add(splitDocument);
        simpleVectorStore.save(vectorStoreFile);

        return simpleVectorStore;
    }

    private List<Document> getSplitDocument() {
        TextReader textReader = new TextReader(kuUniRegisterInfo);
        textReader.getCustomMetadata().put("filename", "ku-uni-register.txt");
        List<Document> documents = textReader.get();

        return new TokenTextSplitter().apply(documents);
    }

    private File getVectorStoreFile() {
        Path path = Paths.get("src", "main", "resources", "ai", "data");
        String absolutePath = path.toFile().getAbsolutePath() + "/" + vectorStoreName;
        return new File(absolutePath);
    }
}
