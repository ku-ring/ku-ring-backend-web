package com.kustacks.kuring.admin.common.dto;

import com.kustacks.kuring.user.domain.Feedback;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedbackDto {

    private String contents;
    private Long userId;
    private LocalDateTime createdAt;

    public static FeedbackDto from(Feedback feedback) {
        return new FeedbackDto(feedback.getContent(), feedback.getUserId(), feedback.getCreatedAt());
    }
}
