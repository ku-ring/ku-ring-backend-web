package com.kustacks.kuring.user.adapter.in.web.dto;

import javax.validation.constraints.NotBlank;

public record UserBookmarkRequest(
        @NotBlank String articleId
) {
}
