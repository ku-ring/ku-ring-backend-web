package com.kustacks.kuring.notice.application.port.in;

public interface NoticeCommandUseCase {

    void createNotice(NoticeCommentCreateCommand command);

    record NoticeCommentCreateCommand(
            String userToken,
            Long noticeId,
            String content
    ) {
    }
}
