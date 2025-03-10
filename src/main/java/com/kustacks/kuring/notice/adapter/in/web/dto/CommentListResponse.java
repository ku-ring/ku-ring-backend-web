package com.kustacks.kuring.notice.adapter.in.web.dto;

import com.kustacks.kuring.notice.application.port.in.NoticeCommentReadingUseCase.CommentAndSubCommentsResult;

import java.util.List;

public record CommentListResponse(
        List<CommentAndSubCommentsResult> comments,
        String endCursor,
        boolean hasNext
) {
}
