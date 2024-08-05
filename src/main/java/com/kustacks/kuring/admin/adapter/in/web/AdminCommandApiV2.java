package com.kustacks.kuring.admin.adapter.in.web;

import com.google.firebase.database.annotations.NotNull;
import com.kustacks.kuring.admin.adapter.in.web.dto.AdminAlertCreateRequest;
import com.kustacks.kuring.admin.adapter.in.web.dto.RealNotificationRequest;
import com.kustacks.kuring.admin.adapter.in.web.dto.TestNotificationRequest;
import com.kustacks.kuring.admin.application.port.in.AdminCommandUseCase;
import com.kustacks.kuring.admin.application.port.in.dto.RealNotificationCommand;
import com.kustacks.kuring.admin.domain.AdminRole;
import com.kustacks.kuring.alert.application.port.in.dto.AlertCreateCommand;
import com.kustacks.kuring.auth.authorization.AuthenticationPrincipal;
import com.kustacks.kuring.auth.context.Authentication;
import com.kustacks.kuring.auth.secured.Secured;
import com.kustacks.kuring.common.dto.BaseResponse;
import com.kustacks.kuring.common.utils.converter.StringToDateTimeConverter;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.ADMIN_REAL_NOTICE_CREATE_SUCCESS;
import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.ADMIN_TEST_NOTICE_CREATE_SUCCESS;

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

    @Operation(summary = "예약 알림 등록", description = "서버에 예약 알림을 등록한다")
    @SecurityRequirement(name = "JWT")
    @Secured(AdminRole.ROLE_ROOT)
    @PostMapping("/alerts")
    public ResponseEntity<BaseResponse<String>> createAlert(
            @RequestBody AdminAlertCreateRequest request
    ) {
        AlertCreateCommand command = new AlertCreateCommand(
                request.title(), request.content(),
                StringToDateTimeConverter.convert(request.alertTime())
        );

        adminCommandUseCase.addAlertSchedule(command);
        return ResponseEntity.ok().body(new BaseResponse<>(ADMIN_REAL_NOTICE_CREATE_SUCCESS, null));
    }

    @Operation(summary = "예약 알림 삭제", description = "서버에 예약되어 있는 특정 알림을 삭제한다")
    @SecurityRequirement(name = "JWT")
    @Secured(AdminRole.ROLE_ROOT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/alerts/{id}")
    public void cancelAlert(
            @Parameter(description = "알림 아이디") @NotNull @PathVariable("id") Long id
    ) {
        adminCommandUseCase.cancelAlertSchedule(id);
    }

    @Hidden
    @Secured(AdminRole.ROLE_ROOT)
    @GetMapping("/subscribe/all")
    public ResponseEntity<Void> subscribe() {
        adminCommandUseCase.subscribeAllUserSameTopic();
        return ResponseEntity.ok().build();
    }
}
