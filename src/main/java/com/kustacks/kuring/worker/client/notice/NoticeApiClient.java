package com.kustacks.kuring.worker.client.notice;

import com.kustacks.kuring.common.error.InternalLogicException;
import com.kustacks.kuring.worker.CategoryName;
import com.kustacks.kuring.worker.client.ApiClient;
import com.kustacks.kuring.worker.update.notice.dto.response.CommonNoticeFormatDTO;

import java.util.List;

// TODO: support(CategoryName) 필요
public interface NoticeApiClient extends ApiClient {

    List<CommonNoticeFormatDTO> getNotices(CategoryName categoryName) throws InternalLogicException;
}
