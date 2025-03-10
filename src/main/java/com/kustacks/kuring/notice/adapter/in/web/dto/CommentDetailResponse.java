package com.kustacks.kuring.notice.adapter.in.web.dto;

import com.kustacks.kuring.notice.application.port.out.dto.CommentReadModel;

import java.time.LocalDateTime;

public record CommentDetailResponse(
        Long id,
        Long parentId,
        Long userId,
        Long noticeId,
        String content,
        LocalDateTime destroyedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static CommentDetailResponse of(CommentReadModel model) {
        return new CommentDetailResponse(
                model.getId(),
                model.getParentId(),
                model.getUserId(),
                model.getNoticeId(),
                model.getContent(),
                model.getDestroyedAt(),
                model.getCreatedAt(),
                model.getUpdatedAt()
        );
    }
}
