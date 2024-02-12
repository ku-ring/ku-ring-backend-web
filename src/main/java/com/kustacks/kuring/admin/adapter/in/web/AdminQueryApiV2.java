package com.kustacks.kuring.admin.adapter.in.web;

import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.AUTH_AUTHENTICATION_SUCCESS;
import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.FEEDBACK_SEARCH_SUCCESS;

import com.kustacks.kuring.admin.application.port.in.AdminQueryUseCase;
import com.kustacks.kuring.admin.domain.AdminRole;
import com.kustacks.kuring.auth.authorization.AuthenticationPrincipal;
import com.kustacks.kuring.auth.context.Authentication;
import com.kustacks.kuring.auth.secured.Secured;
import com.kustacks.kuring.common.dto.BaseResponse;
import com.kustacks.kuring.user.application.port.in.dto.AdminFeedbacksResult;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin-Query", description = "관리자가 주체가 되는 정보 조회")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v2/admin", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminQueryApiV2 {

    private final AdminQueryUseCase adminQueryUseCase;

    @Operation(summary = "피드백 조회", description = "사용자의 모든 피드백을 조회합니다")
    @SecurityRequirement(name = "JWT")
    @Secured(AdminRole.ROLE_ROOT)
    @GetMapping("/feedbacks")
    public ResponseEntity<BaseResponse<List<AdminFeedbacksResult>>> getFeedbacks(
        @Parameter(description = "페이지") @RequestParam(name = "page") @Min(0) int page,
        @Parameter(description = "단일 페이지의 사이즈, 1 ~ 30까지 허용") @RequestParam(name = "size") @Min(1) @Max(30) int size) {
        List<AdminFeedbacksResult> feedbacks = adminQueryUseCase.lookupFeedbacks(page, size);
        return ResponseEntity.ok().body(new BaseResponse<>(FEEDBACK_SEARCH_SUCCESS, feedbacks));
    }

    /**
     * Root 이상만 호출 가능한 테스트 API
     *
     * @return OK
     */
    @Hidden
    @Secured(AdminRole.ROLE_ROOT)
    @GetMapping("/root")
    public ResponseEntity<BaseResponse<List<String>>> roleAdminRoot(
        @AuthenticationPrincipal Authentication authentication
    ) {
        return ResponseEntity.ok()
            .body(new BaseResponse<>(AUTH_AUTHENTICATION_SUCCESS, authentication.getAuthorities()));
    }

    /**
     * Client 이상만 호출 가능한 테스트 API
     *
     * @return OK
     */
    @Hidden
    @Secured(AdminRole.ROLE_CLIENT)
    @GetMapping("/client")
    public ResponseEntity<BaseResponse<List<String>>> roleAdminClient(
        @AuthenticationPrincipal Authentication authentication
    ) {
        return ResponseEntity.ok()
            .body(new BaseResponse<>(AUTH_AUTHENTICATION_SUCCESS, authentication.getAuthorities()));
    }
}
