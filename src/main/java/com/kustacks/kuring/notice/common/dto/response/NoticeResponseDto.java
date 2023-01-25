package com.kustacks.kuring.notice.common.dto.response;

import com.kustacks.kuring.common.dto.ResponseDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeResponseDto extends ResponseDto {

    private String baseUrl;

    private List<NoticeDto> noticeList;

    public NoticeResponseDto(String baseUrl, List<NoticeDto> noticeList) {
        super(true, "성공", 200);
        this.baseUrl = baseUrl;
        this.noticeList = noticeList;
    }
}
