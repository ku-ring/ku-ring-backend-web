package com.kustacks.kuring.admin.adapter.in.web;

import com.kustacks.kuring.admin.application.port.in.dto.RealNotificationCommand;
import com.kustacks.kuring.admin.adapter.in.web.dto.RealNotificationRequest;
import com.kustacks.kuring.admin.adapter.in.web.dto.TestNotificationRequest;
import com.kustacks.kuring.admin.application.port.in.AdminCommandUseCase;
import com.kustacks.kuring.admin.domain.AdminRole;
import com.kustacks.kuring.auth.authorization.AuthenticationPrincipal;
import com.kustacks.kuring.auth.context.Authentication;
import com.kustacks.kuring.auth.secured.Secured;
import com.kustacks.kuring.common.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.ADMIN_REAL_NOTICE_CREATE_SUCCESS;
import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.ADMIN_TEST_NOTICE_CREATE_SUCCESS;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v2/admin", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminCommandApiV2 {

    private final AdminCommandUseCase adminCommandUseCase;

    @Secured(AdminRole.ROLE_ROOT)
    @PostMapping("/notices/dev")
    public ResponseEntity<BaseResponse<String>> createTestNotice(
            @RequestBody TestNotificationRequest request
    ) {
        adminCommandUseCase.createTestNotice(request.toCommand());
        return ResponseEntity.ok().body(new BaseResponse<>(ADMIN_TEST_NOTICE_CREATE_SUCCESS, null));
    }

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
