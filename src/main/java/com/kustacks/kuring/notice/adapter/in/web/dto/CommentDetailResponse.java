package com.kustacks.kuring.notice.adapter.in.web.dto;

import com.kustacks.kuring.notice.application.port.out.dto.CommentReadModel;

import java.time.LocalDateTime;

public record CommentDetailResponse(
        Long id,
        Long parentId,
        Long userId,
        String nickName,
        Long noticeId,
        String content,
        boolean isMine,
        LocalDateTime destroyedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static CommentDetailResponse of(CommentReadModel model, Long currentUserId) {
        return new CommentDetailResponse(
                model.getId(),
                model.getParentId(),
                model.getUserId(),
                model.getNickName(),
                model.getNoticeId(),
                model.getContent(),
                currentUserId != null && currentUserId.equals(model.getUserId()),
                model.getDestroyedAt(),
                model.getCreatedAt(),
                model.getUpdatedAt()
        );
    }
}
