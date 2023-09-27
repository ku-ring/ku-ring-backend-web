package com.kustacks.kuring.admin.presentation;

import com.kustacks.kuring.admin.domain.AdminRole;
import com.kustacks.kuring.auth.authorization.AuthenticationPrincipal;
import com.kustacks.kuring.auth.context.Authentication;
import com.kustacks.kuring.auth.secured.Secured;
import com.kustacks.kuring.common.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    /**
     * Root 이상만 호출 가능한 테스트 API
     *
     * @return
     */
    @Secured(AdminRole.ROLE_ROOT)
    @GetMapping("root")
    public ResponseEntity<BaseResponse<List<String>>> roleAdminRoot(@AuthenticationPrincipal Authentication authentication) {
        return ResponseEntity.ok().body(new BaseResponse<>(AUTH_AUTHENTICATION_SUCCESS, authentication.getAuthorities()));
    }

    /**
     * Client 이상만 호출 가능한 테스트 API
     * @return
     */
    @Secured(AdminRole.ROLE_CLIENT)
    @GetMapping("client")
    public ResponseEntity<BaseResponse<List<String>>> roleAdminClient(@AuthenticationPrincipal Authentication authentication) {
        return ResponseEntity.ok().body(new BaseResponse<>(AUTH_AUTHENTICATION_SUCCESS, authentication.getAuthorities()));
    }
}
