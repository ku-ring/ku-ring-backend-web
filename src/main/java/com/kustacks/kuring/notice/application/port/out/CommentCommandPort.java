package com.kustacks.kuring.notice.application.port.out;

public interface CommentCommandPort {

    void createComment(Long userId, Long noticeId, String content);
}
