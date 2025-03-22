package com.kustacks.kuring.notice.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record NoticeCommentCreateRequest(
        @Schema(description = "댓글 내용을 작성합니다", example = "댓글내용")
        String content,
        @Schema(description = "부모 댓글의 아이디 값 전달시 대댓글 생성. null전달시 그냥 댓글 생성", example = "1")
        Long parentId
) {
}
