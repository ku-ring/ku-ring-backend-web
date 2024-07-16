package com.kustacks.kuring.worker.parser.notice;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PageTextDto {

    private final String title;
    private final String articleId;
    private final String text;
}
