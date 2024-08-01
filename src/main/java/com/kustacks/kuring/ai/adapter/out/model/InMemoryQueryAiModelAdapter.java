package com.kustacks.kuring.ai.adapter.out.model;

import com.kustacks.kuring.ai.application.port.out.QueryAiModelPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Slf4j
@Component
@Profile("dev | test")
@RequiredArgsConstructor
public class InMemoryQueryAiModelAdapter implements QueryAiModelPort {

    @Override
    public Flux<String> call(Prompt prompt) {
        if (prompt.getContents().contains("교내,외 장학금 및 학자금 대출 관련 전화번호들을 안내를 해줘")) {
            return Flux.just("학", "생", "복", "지", "처", " ", "장", "학", "복", "지", "팀", "의",
                    " ", "전", "화", "번", "호", "는", " ", "0", "2", "-", "4", "5", "0", "-", "3", "2", "1",
                    "1", "~", "2", "이", "며", ",", " ", "건", "국", "사", "랑", "/", "장", "학", "사", "정",
                    "관", "장", "학", "/", "기", "금", "장", "학", "과", " ", "관", "련", "된", " ", "문", "의",
                    "는", " ", "0", "2", "-", "4", "5", "0", "-", "3", "9", "6", "7", "로", " ", "하", "시",
                    "면", " ", "됩", "니", "다", "."
            );
        }

        return Flux.just("미", "리", " ", "준", "비", "된", " ",
                "테", "스", "트", "질", "문", "이", " ", "아", "닙", "니", "다");
    }
}
