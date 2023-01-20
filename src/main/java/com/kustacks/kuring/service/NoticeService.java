package com.kustacks.kuring.service;

import com.kustacks.kuring.controller.dto.NoticeDTO;
import com.kustacks.kuring.notice.domain.Notice;

import java.util.List;

public interface NoticeService {

    List<NoticeDTO> getNotices(String type, int offset, int max);
    List<Notice> handleSearchRequest(String keywords);
}
