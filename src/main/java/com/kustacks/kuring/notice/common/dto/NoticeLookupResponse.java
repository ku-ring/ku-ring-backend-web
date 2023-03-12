package com.kustacks.kuring.notice.common.dto;

import com.kustacks.kuring.search.common.dto.NoticeSearchDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NoticeLookupResponse {

    private List<NoticeSearchDto> noticeList;
}
