package com.kustacks.kuring.notice.application.port.in;

public interface NoticeCommentWritingUseCase {

    void process(WriteCommentCommand command);

    void process(WriteReplyCommand command);

    record WriteCommentCommand(
            String userToken,
            Long noticeId,
            String content
    ) {
    }

    record WriteReplyCommand(
            String userToken,
            Long noticeId,
            String content,
            Long parentId
    ) {
    }
}
