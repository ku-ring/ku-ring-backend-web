package com.kustacks.kuring.user.adapter.in.web.dto;

import javax.validation.constraints.NotNull;

public record UserAcademicEventNotificationRequest(
        @NotNull Boolean enabled
) {
}