package com.kustacks.kuring.notice.application.port.in;

public interface NoticeCommentWritingUseCase {

    void process(WriteCommentCommand command);

    void process(WriteReplyCommand command);

    record WriteCommentCommand(
            String email,
            Long noticeId,
            String content
    ) {
    }

    record WriteReplyCommand(
            String email,
            Long noticeId,
            String content,
            Long parentId
    ) {
    }
}
