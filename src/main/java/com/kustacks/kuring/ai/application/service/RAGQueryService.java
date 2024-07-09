package com.kustacks.kuring.ai.application.service;

import com.kustacks.kuring.ai.application.port.in.RAGQueryUseCase;
import com.kustacks.kuring.ai.application.port.out.RAGQueryAiModelPort;
import com.kustacks.kuring.ai.application.port.out.RAGQuerySimilarityPort;
import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.common.domain.Events;
import com.kustacks.kuring.common.exception.InvalidStateException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.user.adapter.in.event.dto.UserDecreaseQuestionCountEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UseCase
@RequiredArgsConstructor
public class RAGQueryService implements RAGQueryUseCase {

    private final RAGQuerySimilarityPort ragSimilarityPort;
    private final RAGQueryAiModelPort ragChatModel;

    @Value("classpath:/ai/prompts/rag-prompt-template.st")
    private Resource ragPromptTemplate;
    private PromptTemplate promptTemplate;

    @Override
    public String askAiModel(String question, String id) {
        Prompt completePrompt = buildCompletePrompt(question);
        Events.raise(new UserDecreaseQuestionCountEvent(id));
        return ragChatModel.call(completePrompt);
    }

    @PostConstruct
    private void init() {
        this.promptTemplate = new PromptTemplate(ragPromptTemplate);
    }

    private Prompt buildCompletePrompt(String question) {
        List<String> similarDocuments = ragSimilarityPort.findSimilarityContents(question);
        if(similarDocuments.isEmpty()) {
            throw new InvalidStateException(ErrorCode.AI_SIMILAR_DOCUMENTS_NOT_FOUND);
        }

        Map<String, Object> promptParameters = createQuestions(question, similarDocuments);
        return promptTemplate.create(promptParameters);
    }

    private Map<String, Object> createQuestions(String question, List<String> contentList) {
        Map<String, Object> promptParameters = new HashMap<>();
        promptParameters.put("input", question);
        promptParameters.put("documents", String.join("In", contentList));
        return promptParameters;
    }
}
