package com.kustacks.kuring.notice.application.port.out.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentReadModel {

    private final Long id;
    private final Long parentId;
    private final Long userId;
    private final String nickName;
    private final Long noticeId;
    private final String content;
    private final LocalDateTime destroyedAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @QueryProjection
    public CommentReadModel(Long id, Long parentId, Long userId, String nickName, Long noticeId, String content,
                            LocalDateTime destroyedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.parentId = parentId;
        this.userId = userId;
        this.nickName = nickName;
        this.noticeId = noticeId;
        this.content = content;
        this.destroyedAt = destroyedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;

    }
}
