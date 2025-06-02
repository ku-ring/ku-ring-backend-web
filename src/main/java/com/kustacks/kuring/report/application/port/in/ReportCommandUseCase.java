package com.kustacks.kuring.report.application.port.in;

import com.kustacks.kuring.report.domain.ReportTargetType;

public interface ReportCommandUseCase {

    void process(ReportCommentCommand command);

    record ReportCommentCommand(
            Long targetId,
            String userToken,
            String content,
            ReportTargetType targetType
    ) {
    }
}