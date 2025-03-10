package com.kustacks.kuring.notice.application.port.in;

import com.kustacks.kuring.common.data.Cursor;
import com.kustacks.kuring.common.data.CursorBasedList;
import com.kustacks.kuring.notice.adapter.in.web.dto.CommentDetailResponse;

import java.util.List;

public interface NoticeCommentReadingUseCase {

    /**
     * @param noticeId 공지의 아이디 값
     * @param cursor 조회할 댓글의 시작 커서 값
     * @param size 조회할 댓글의 사이즈 값
     */
    CursorBasedList<CommentAndSubCommentsResult> findComments(Long noticeId, Cursor cursor, int size);

    record CommentAndSubCommentsResult(
            CommentDetailResponse comment,
            List<CommentDetailResponse> subComments
    ) {
    }
}
