package com.kustacks.kuring.report.application.service;

import com.kustacks.kuring.common.annotation.UseCase;
import com.kustacks.kuring.common.exception.NotFoundException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.notice.application.port.out.CommentQueryPort;
import com.kustacks.kuring.notice.domain.Comment;
import com.kustacks.kuring.report.application.port.in.ReportCommandUseCase;
import com.kustacks.kuring.report.application.port.out.ReportCommandPort;
import com.kustacks.kuring.report.application.port.out.ReportQueryPort;
import com.kustacks.kuring.report.application.service.exception.ReportBusinessException;
import com.kustacks.kuring.report.domain.Report;
import com.kustacks.kuring.user.application.port.out.UserQueryPort;
import com.kustacks.kuring.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
class ReportCommandService implements ReportCommandUseCase {

    private final ReportCommandPort reportCommandPort;
    private final ReportQueryPort reportQueryPort;
    private final UserQueryPort userQueryPort;
    private final CommentQueryPort commentQueryPort;

    @Override
    public void process(ReportCommentCommand command) {
        User reporter = findUserByTokenOrThrow(command.userToken());
        findCommentByIdOrThrow(command.targetId());
        reportQueryPort.findByReporterAndTargetIdAndType(reporter, command.targetId(), command.targetType())
                .ifPresentOrElse(
                        report -> {
                            throw new ReportBusinessException(ErrorCode.REPORT_COMMENT_DUPLICATE);
                        },
                        () -> {
                            reportCommandPort.save(new Report(command.targetId(), command.content(), reporter, command.targetType()));
                        }
                );
    }

    private Comment findCommentByIdOrThrow(Long commentId) {
        return commentQueryPort.findById(commentId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.COMMENT_NOT_FOUND));
    }

    private User findUserByTokenOrThrow(String token) {
        return userQueryPort.findByToken(token)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    }
}
