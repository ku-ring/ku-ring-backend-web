package com.kustacks.kuring.user.application.port.out.dto;

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
    public FeedbackDto(String contents, Long userId, LocalDateTime createdAt) {
        this.contents = contents;
        this.userId = userId;
        this.createdAt = createdAt;
    }
}
