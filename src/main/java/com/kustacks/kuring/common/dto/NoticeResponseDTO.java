package com.kustacks.kuring.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class NoticeResponseDTO extends ResponseDTO {

    @JsonProperty("baseUrl")
    private String baseUrl;

    @JsonProperty("noticeList")
    private List<NoticeDTO> noticeList;

    public NoticeResponseDTO(String baseUrl, List<NoticeDTO> noticeList) {
        super(true, "성공", 200);
        this.baseUrl = baseUrl;
        this.noticeList = noticeList;
    }
}
