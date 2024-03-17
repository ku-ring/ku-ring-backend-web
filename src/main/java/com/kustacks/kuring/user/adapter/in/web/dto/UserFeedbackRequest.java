package com.kustacks.kuring.user.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserFeedbackRequest(
        @NotBlank @Size(min = 5, max = 256) String content
) {
}
