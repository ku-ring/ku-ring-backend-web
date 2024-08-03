package com.kustacks.kuring.alert.adapter.in.web;

import com.google.firebase.database.annotations.NotNull;
import com.kustacks.kuring.alert.adapter.in.web.dto.AlertCreateRequest;
import com.kustacks.kuring.alert.application.port.in.AlertCommandUseCase;
import com.kustacks.kuring.alert.application.port.in.dto.AlertCreateCommand;
import com.kustacks.kuring.common.annotation.RestWebAdapter;
import com.kustacks.kuring.common.utils.converter.StringToDateTimeConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Alert-Command", description = "알림 에약")
@Validated
@RequiredArgsConstructor
@RestWebAdapter(path = "/api/v2/alerts")
public class AlertCommandApiV2 {

    private final AlertCommandUseCase alertCommandUseCase;

    @Operation(summary = "예약 알림 등록", description = "서버에 예약 알림을 등록한다")
    @PostMapping
    public void createAlert(
            @RequestBody AlertCreateRequest request
    ) {
        AlertCreateCommand command = new AlertCreateCommand(
                request.title(), request.content(),
                StringToDateTimeConverter.convert(request.alertTime())
        );

        alertCommandUseCase.addAlertSchedule(command);
    }

    @Operation(summary = "예약 알림 삭제", description = "서버에 예약되어 있는 특정 알림을 삭제한다")
    @DeleteMapping("/{id}")
    public void cancelAlert(
            @Parameter(description = "알림 아이디") @NotNull @PathVariable Long id
    ) {
        alertCommandUseCase.cancelAlertSchedule(id);
    }
}
