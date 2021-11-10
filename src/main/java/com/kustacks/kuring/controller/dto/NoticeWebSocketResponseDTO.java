package com.kustacks.kuring.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class NoticeWebSocketResponseDTO extends WebSocketResponseDTO {

    @JsonProperty("noticeList")
    private List<NoticeSearchDTO> noticeList;

    public NoticeWebSocketResponseDTO(List<NoticeSearchDTO> noticeList) {
        super(true, "성공", 200, "notice");
        this.noticeList = noticeList;
    }
}
