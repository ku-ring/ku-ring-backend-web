package com.kustacks.kuring.alert.adapter.in.web.dto;

import jakarta.validation.constraints.NotEmpty;

public record AlertCreateRequest(
        @NotEmpty(message = "제목은 필수입니다")
        String title,
        String content,
        @NotEmpty(message = "알림 시간은 필수입니다")
        String alertTime
) {
}
