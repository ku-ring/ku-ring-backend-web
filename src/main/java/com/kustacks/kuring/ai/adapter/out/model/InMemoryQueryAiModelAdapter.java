package com.kustacks.kuring.ai.adapter.out.model;

import com.kustacks.kuring.ai.application.port.out.QueryAiModelPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@Component
@Profile("dev | local | test")
@RequiredArgsConstructor
public class InMemoryQueryAiModelAdapter implements QueryAiModelPort {

    @Value("classpath:/ai/docs/ku-uni-register.txt")
    private Resource kuUniRegisterInfo;

    @Override
    public Flux<String> call(Prompt prompt) {
        if (prompt.getContents().contains("교내,외 장학금 및 학자금 대출 관련 전화번호들을 안내를 해줘")) {
            return Flux.just("학생복지처 ", "장학복지팀의 ", "전화번호는 ", "02-450-3211~2이며, ",
                    "건국사랑/장학사정관장학/기금장학과 ", "관련된 ", "문의는 02-450-3967로 하시면 됩니다.");
        }

        if (prompt.getContents().contains("2학기 등록금 납부일을 알려줘")) {
            return Flux.just("1차 등록일은", "2024. 8. 19.(월) 09:00 ~ 8. 23.(금) 16:00",
                    "2차 등록일은", "2024. 9. 2.(월) 09:00 ~ 9. 6.(금) 16:00"
            );
        }

        if (prompt.getContents().contains("긴 응답 테스트")) {
            TextReader textReader = new TextReader(kuUniRegisterInfo);
            List<Document> read = textReader.read();

            return Flux.fromIterable(read)
                    .flatMap(document -> Flux.just(document.getContent()))
                    .onErrorStop();
        }

        return Flux.just("미리 준비된 테스트 질문이 아닙니다");
    }
}
