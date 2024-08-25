package com.kustacks.kuring.user.adapter.in.web.dto;

import com.kustacks.kuring.user.application.port.in.dto.UserAIAskCountResult;

public record UserAIAskCountResponse(
        int leftAskCount,
        int maxAskCount
) {
    public static UserAIAskCountResponse from(UserAIAskCountResult result) {
        return new UserAIAskCountResponse(result.leftAskCount(), result.maxAskCount());
    }
}
