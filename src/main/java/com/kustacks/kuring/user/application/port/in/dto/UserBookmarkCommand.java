package com.kustacks.kuring.user.application.port.in.dto;

public record UserBookmarkCommand(
        String userToken,
        String articleId
) {
}
