package com.kustacks.kuring.user.adapter.in.web.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record UserFeedbackRequest(
        @NotBlank @Size(min = 5, max = 256) String content
) {
}
