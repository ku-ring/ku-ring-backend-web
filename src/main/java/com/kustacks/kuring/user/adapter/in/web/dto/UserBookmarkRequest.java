package com.kustacks.kuring.user.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record UserBookmarkRequest(
        @NotBlank String articleId
) {
}
