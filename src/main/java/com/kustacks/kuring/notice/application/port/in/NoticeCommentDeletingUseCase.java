package com.kustacks.kuring.notice.application.port.in;

public interface NoticeCommentDeletingUseCase {

    void process(DeleteCommentCommand command);

    record DeleteCommentCommand(
            String userToken,
            Long noticeId,
            Long commentId
    ) {
    }
}
