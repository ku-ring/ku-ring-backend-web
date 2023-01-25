package com.kustacks.kuring.search.common.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class NoticeWebSocketResponseDto extends WebSocketResponseDto {

    private List<NoticeSearchDto> noticeList;

    public NoticeWebSocketResponseDto(List<NoticeSearchDto> noticeList) {
        super(true, "성공", 200, "notice");
        this.noticeList = noticeList;
    }
}
