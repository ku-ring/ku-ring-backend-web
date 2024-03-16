package com.kustacks.kuring.ai.adapter.in.web;

import com.kustacks.kuring.ai.application.service.AIQueryService;
import com.kustacks.kuring.common.annotation.RestWebAdapter;
import com.kustacks.kuring.common.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.NOTICE_SEARCH_SUCCESS;

@RequiredArgsConstructor
@RestWebAdapter(path = "/api/v2/notices/ai")
public class AIQueryApiV1 {

    private final AIQueryService aiQueryService;

    @GetMapping
    public ResponseEntity<BaseResponse> getAIQuery(@RequestParam(value = "message") String questions) {
        String answer = aiQueryService.getAIQuery(questions);
        return ResponseEntity.ok().body(new BaseResponse<>(NOTICE_SEARCH_SUCCESS, answer));
    }

    @GetMapping("/embedding")
    public ResponseEntity<BaseResponse> embeddedQuestions() {
        aiQueryService.embedding();
        return ResponseEntity.ok().body(new BaseResponse<>(NOTICE_SEARCH_SUCCESS, null));
    }
}
