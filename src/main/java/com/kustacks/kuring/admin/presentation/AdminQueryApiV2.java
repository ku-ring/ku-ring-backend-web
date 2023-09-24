package com.kustacks.kuring.admin.presentation;

import com.kustacks.kuring.common.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.AUTH_AUTHENTICATION_SUCCESS;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v2/admin/", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminQueryApiV2 {

    /**
     * Auth 필터 확인을 위한 임시 API
     * @return
     */
    @GetMapping("hello")
    public ResponseEntity<BaseResponse<String>> tempApi() {
        return ResponseEntity.ok().body(new BaseResponse<>(AUTH_AUTHENTICATION_SUCCESS, "hello"));
    }
}
