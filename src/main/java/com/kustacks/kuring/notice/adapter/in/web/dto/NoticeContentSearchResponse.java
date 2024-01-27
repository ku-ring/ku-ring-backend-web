package com.kustacks.kuring.notice.adapter.in.web.dto;

import com.kustacks.kuring.notice.application.port.in.dto.NoticeContentSearchResult;

import java.util.List;

public record NoticeContentSearchResponse(
        List<NoticeContentSearchResult> noticeList
) {
}
