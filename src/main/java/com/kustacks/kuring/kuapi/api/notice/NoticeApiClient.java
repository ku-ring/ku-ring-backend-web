package com.kustacks.kuring.kuapi.api.notice;

import com.kustacks.kuring.common.error.InternalLogicException;
import com.kustacks.kuring.kuapi.CategoryName;
import com.kustacks.kuring.kuapi.api.ApiClient;
import com.kustacks.kuring.kuapi.notice.dto.response.CommonNoticeFormatDTO;

import java.util.List;

// TODO: support(CategoryName) 필요
public interface NoticeApiClient extends ApiClient {

    List<CommonNoticeFormatDTO> getNotices(CategoryName categoryName) throws InternalLogicException;
}