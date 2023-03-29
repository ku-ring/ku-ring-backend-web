package com.kustacks.kuring.notice.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NoticeListLookupResponse {

    private List<NoticeDto> noticeList;
}
