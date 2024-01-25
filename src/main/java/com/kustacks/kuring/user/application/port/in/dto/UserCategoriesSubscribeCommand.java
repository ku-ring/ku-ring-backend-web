package com.kustacks.kuring.user.application.port.in.dto;

import java.util.List;

public record UserCategoriesSubscribeCommand(
        String userToken,
        List<String> categories
) {
}
