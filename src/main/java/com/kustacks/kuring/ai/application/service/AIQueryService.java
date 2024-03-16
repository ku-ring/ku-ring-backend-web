package com.kustacks.kuring.ai.application.service;

import com.kustacks.kuring.common.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorsore.ChromaVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UseCase
@RequiredArgsConstructor
public class AIQueryService {

    private final String template = """
            You are helping to answer questions about services provided by Kuring.
            Kuring is a service that shows announcements from Korea Konkuk University.
                    
            Use the information from the DOCUMENTS section to provide accurate answers but act as if you knew this information innately.
            If unsure, simply state that you don't know.
                    
            Please answer in Korean.
            
            DOCUMENTS:
            {documents}
                        
            """;

    private final OpenAiChatClient aiClient;
    private final ChromaVectorStore chromaVectorStore;


    @Value("classpath:text-source.txt") // This is the text document to load
    private Resource resource;

    public String getAIQuery(String question) {
        var listOfSimilarDocuments = chromaVectorStore.similaritySearch(question);

        var documents = listOfSimilarDocuments
                .stream()
                .map(Document::getContent)
                .collect(Collectors.joining(System.lineSeparator()));

        var systemMessage = new SystemPromptTemplate(this.template)
                .createMessage(Map.of("documents", documents));

        var userMessage = new UserMessage(question);

        var aiResponse = aiClient.call(new Prompt(List.of(systemMessage, userMessage)));
        return aiResponse.getResult().getOutput().getContent();
    }

    public void embedding() {
        var textSplitter = new TokenTextSplitter();
        List<Document> loadText = loadText();
        List<Document> documents = textSplitter.apply(loadText);
        chromaVectorStore.accept(documents);
    }

    private List<Document> loadText() {
        TextReader textReader = new TextReader(resource);
        textReader.getCustomMetadata().put("filename", "text-source.txt");
        return textReader.get();
    }
}
