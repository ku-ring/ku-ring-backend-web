package com.kustacks.kuring.user.application.port.out.dto;

import com.kustacks.kuring.common.domain.Content;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedbackDto {

    private String contents;
    private Long userId;
    private LocalDateTime createdAt;

    @QueryProjection
    public FeedbackDto(Content contents, Long userId, LocalDateTime createdAt) {
        this.contents = contents.getValue();
        this.userId = userId;
        this.createdAt = createdAt;
    }
}
