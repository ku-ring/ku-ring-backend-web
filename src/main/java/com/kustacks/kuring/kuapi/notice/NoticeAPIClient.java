package com.kustacks.kuring.kuapi.notice;

import com.kustacks.kuring.kuapi.CategoryName;
import com.kustacks.kuring.kuapi.notice.dto.response.CommonNoticeFormatDTO;

import java.util.List;

public interface NoticeAPIClient {
    List<CommonNoticeFormatDTO> getNotices(CategoryName categoryName) throws InterruptedException;
}
