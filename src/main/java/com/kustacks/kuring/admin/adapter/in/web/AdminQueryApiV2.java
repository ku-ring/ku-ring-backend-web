package com.kustacks.kuring.admin.adapter.in.web;

import com.kustacks.kuring.admin.adapter.in.web.dto.AdminAlertListResponse;
import com.kustacks.kuring.admin.adapter.in.web.dto.AdminFeedbackListResponse;
import com.kustacks.kuring.admin.adapter.in.web.dto.AdminReportListResponse;
import com.kustacks.kuring.admin.application.port.in.AdminQueryUseCase;
import com.kustacks.kuring.admin.domain.AdminRole;
import com.kustacks.kuring.auth.authorization.AuthenticationPrincipal;
import com.kustacks.kuring.auth.context.Authentication;
import com.kustacks.kuring.auth.secured.Secured;
import com.kustacks.kuring.common.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.*;

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
    public ResponseEntity<BaseResponse<AdminFeedbackListResponse>> getFeedbacks(
        @Parameter(description = "페이지") @RequestParam(name = "page") @Min(0) int page,
        @Parameter(description = "단일 페이지의 사이즈, 1 ~ 30까지 허용") @RequestParam(name = "size") @Min(1) @Max(30) int size
    ) {
        var pageResult = adminQueryUseCase.lookupFeedbacks(page, size);
        AdminFeedbackListResponse response = AdminFeedbackListResponse.from(pageResult);
        return ResponseEntity.ok().body(new BaseResponse<>(FEEDBACK_SEARCH_SUCCESS, response));
    }

    @Operation(summary = "예약 알림 조회", description = "어드민이 등록한 모든 예약 알림을 조회한다")
    @SecurityRequirement(name = "JWT")
    @Secured(AdminRole.ROLE_ROOT)
    @GetMapping("/alerts")
    public ResponseEntity<BaseResponse<AdminAlertListResponse>> getAlerts(
            @Parameter(description = "페이지") @RequestParam(name = "page") @Min(0) int page,
            @Parameter(description = "단일 페이지의 사이즈, 1 ~ 30까지 허용") @RequestParam(name = "size") @Min(1) @Max(30) int size
    ) {
        var pageResult = adminQueryUseCase.lookupAlerts(page, size);
        AdminAlertListResponse response = AdminAlertListResponse.from(pageResult);
        return ResponseEntity.ok().body(new BaseResponse<>(ALERT_SEARCH_SUCCESS, response));
    }

    @Operation(summary = "신고 목록 조회", description = "사용자의 모든 신고 목록을 조회합니다")
    @SecurityRequirement(name = "JWT")
    @Secured(AdminRole.ROLE_ROOT)
    @GetMapping("/reports")
    public ResponseEntity<BaseResponse<AdminReportListResponse>> getReports(
            @Parameter(description = "페이지") @RequestParam(name = "page") @Min(0) int page,
            @Parameter(description = "단일 페이지의 사이즈, 1 ~ 30까지 허용") @RequestParam(name = "size") @Min(1) @Max(30) int size
    ) {
        var pageResult = adminQueryUseCase.lookupReports(page, size);
        AdminReportListResponse response = AdminReportListResponse.from(pageResult);
        return ResponseEntity.ok().body(new BaseResponse<>(REPORT_SEARCH_SUCCESS, response));
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
