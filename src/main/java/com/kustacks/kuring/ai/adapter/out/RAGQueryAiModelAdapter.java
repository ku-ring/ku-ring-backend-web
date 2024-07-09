package com.kustacks.kuring.ai.adapter.out;

import com.kustacks.kuring.ai.application.port.out.RAGQueryAiModelPort;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("prod | local | test")
@RequiredArgsConstructor
public class RAGQueryAiModelAdapter implements RAGQueryAiModelPort {

    private final OpenAiChatModel openAiChatModel;

    @Override
    public String call(Prompt prompt) {
        return openAiChatModel
                .call(prompt)
                .getResult()
                .getOutput()
                .getContent();
    }
}
