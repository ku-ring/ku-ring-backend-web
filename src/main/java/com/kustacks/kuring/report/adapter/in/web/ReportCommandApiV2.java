package com.kustacks.kuring.report.adapter.in.web;

import com.kustacks.kuring.common.annotation.RestWebAdapter;
import com.kustacks.kuring.common.dto.BaseResponse;
import com.kustacks.kuring.common.exception.BusinessException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.report.adapter.in.web.dto.ReportRequest;
import com.kustacks.kuring.report.application.port.in.ReportCommandUseCase;
import com.kustacks.kuring.report.application.port.in.ReportCommandUseCase.ReportCommentCommand;
import com.kustacks.kuring.report.domain.ReportTargetType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import static com.kustacks.kuring.common.dto.ResponseCodeAndMessages.REPORT_COMMENT_SUCCESS;
import static com.kustacks.kuring.report.domain.ReportTargetType.COMMENT;

@Tag(name = "Report-Command", description = "신고 기능")
@Validated
@RequiredArgsConstructor
@RestWebAdapter(path = "/api/v2/reports")
public class ReportCommandApiV2 {

    private static final String FCM_TOKEN_HEADER_KEY = "User-Token";

    private final ReportCommandUseCase reportCommandUseCase;

    @Operation(summary = "신고하기", description = "특정 항목을 신고합니다.")
    @PostMapping
    public ResponseEntity<BaseResponse<Void>> report(
            @RequestHeader(FCM_TOKEN_HEADER_KEY) String userToken,
            @RequestBody ReportRequest request
    ) {
        ReportTargetType targetType = ReportTargetType.fromString(request.reportType());
        if (targetType.match(COMMENT)) {
            var command = new ReportCommentCommand(
                    request.targetId(),
                    userToken,
                    request.content(),
                    targetType);

            reportCommandUseCase.process(command);
            return ResponseEntity.status(REPORT_COMMENT_SUCCESS.getCode())
                    .body(new BaseResponse<>(REPORT_COMMENT_SUCCESS, null));
        }
        throw new BusinessException(ErrorCode.REPORT_INVALID_TARGET_TYPE);
    }
}