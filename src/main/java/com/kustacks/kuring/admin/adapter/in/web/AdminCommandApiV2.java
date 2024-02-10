package com.kustacks.kuring.admin.adapter.in.web;

import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.ADMIN_REAL_NOTICE_CREATE_SUCCESS;
import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.ADMIN_TEST_NOTICE_CREATE_SUCCESS;

import com.kustacks.kuring.admin.adapter.in.web.dto.RealNotificationRequest;
import com.kustacks.kuring.admin.adapter.in.web.dto.TestNotificationRequest;
import com.kustacks.kuring.admin.application.port.in.AdminCommandUseCase;
import com.kustacks.kuring.admin.application.port.in.dto.RealNotificationCommand;
import com.kustacks.kuring.admin.domain.AdminRole;
import com.kustacks.kuring.auth.authorization.AuthenticationPrincipal;
import com.kustacks.kuring.auth.context.Authentication;
import com.kustacks.kuring.auth.secured.Secured;
import com.kustacks.kuring.common.dto.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin-Command", description = "관리자가 주체가 되는 정보 수정")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v2/admin", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminCommandApiV2 {

    private final AdminCommandUseCase adminCommandUseCase;

    @Operation(summary = "테스트 공지 전송", description = "테스트 공지를 전송합니다, 실제 운영시 사용하지 않습니다")
    @SecurityRequirement(name = "JWT")
    @Secured(AdminRole.ROLE_ROOT)
    @PostMapping("/notices/dev")
    public ResponseEntity<BaseResponse<String>> createTestNotice(
            @RequestBody TestNotificationRequest request
    ) {
        adminCommandUseCase.createTestNotice(request.toCommand());
        return ResponseEntity.ok().body(new BaseResponse<>(ADMIN_TEST_NOTICE_CREATE_SUCCESS, null));
    }

    @Operation(summary = "전체 공지 전송", description = "전체 공지를 전송합니다, 실제 운영시 사용합니다")
    @SecurityRequirement(name = "JWT")
    @Secured(AdminRole.ROLE_ROOT)
    @PostMapping("/notices/prod")
    public ResponseEntity<BaseResponse<String>> createRealNotice(
            @RequestBody RealNotificationRequest request,
            @AuthenticationPrincipal Authentication authentication
    ) {
        RealNotificationCommand command = request.toCommandWithAuthentication(authentication);
        adminCommandUseCase.createRealNoticeForAllUser(command);
        return ResponseEntity.ok().body(new BaseResponse<>(ADMIN_REAL_NOTICE_CREATE_SUCCESS, null));
    }

    @Secured(AdminRole.ROLE_ROOT)
    @GetMapping("/subscribe/all")
    public ResponseEntity<Void> subscribe() {
        adminCommandUseCase.subscribeAllUserSameTopic();
        return ResponseEntity.ok().build();
    }
}
