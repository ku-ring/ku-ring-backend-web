package com.kustacks.kuring.worker.parser.notice;

public record PageTextDto(
        String title,
        String articleId,
        String date,
        String text
) {
}
