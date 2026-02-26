package com.kustacks.kuring.user.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;

public record UserClubSubscriptionRequest(
        @NotNull Long id
) {
}
