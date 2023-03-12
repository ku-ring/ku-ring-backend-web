package com.kustacks.kuring.notice.common.dto;

import com.kustacks.kuring.common.dto.ResponseDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeListResponse extends ResponseDto {

    private String baseUrl;

    private List<NoticeDto> noticeList;

    public NoticeListResponse(String baseUrl, List<NoticeDto> noticeList) {
        super(true, "성공", 200);
        this.baseUrl = baseUrl;
        this.noticeList = noticeList;
    }
}
