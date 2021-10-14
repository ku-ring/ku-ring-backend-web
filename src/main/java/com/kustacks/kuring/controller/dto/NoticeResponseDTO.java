package com.kustacks.kuring.controller.dto;

import com.kustacks.kuring.domain.notice.Notice;
import lombok.Getter;

import java.util.List;

@Getter
public class NoticeResponseDTO extends ResponseDTO {

    private String baseUrl;
    private List<NoticeDTO> noticeList;

    public NoticeResponseDTO(String baseUrl, List<NoticeDTO> noticeList) {
        super(true, "성공", 200);
        this.baseUrl = baseUrl;
        this.noticeList = noticeList;
    }
}
