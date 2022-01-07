package com.kustacks.kuring.kuapi.api.notice;

import com.kustacks.kuring.error.InternalLogicException;
import com.kustacks.kuring.kuapi.CategoryName;
import com.kustacks.kuring.kuapi.api.APIClient;
import com.kustacks.kuring.kuapi.notice.dto.response.CommonNoticeFormatDTO;

import java.util.List;

// TODO: support(CategoryName) 필요
public interface NoticeAPIClient extends APIClient {

    List<CommonNoticeFormatDTO> getNotices(CategoryName categoryName) throws InternalLogicException;
    void blockLogin();
}
