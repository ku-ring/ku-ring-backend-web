package com.kustacks.kuring.notice.application.port.in;

public interface NoticeCommentEditingUseCase {

    void process(EditCommentCommand command);

    record EditCommentCommand(
            String email,
            Long noticeId,
            Long commentId,
            String content
    ) {
    }
}
