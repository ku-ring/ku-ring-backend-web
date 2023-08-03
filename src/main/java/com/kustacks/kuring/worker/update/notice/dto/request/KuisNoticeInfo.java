package com.kustacks.kuring.worker.update.notice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.worker.scrap.client.notice.KuisNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.NoticeApiClient;
import com.kustacks.kuring.worker.update.notice.dto.response.CommonNoticeFormatDto;

import java.util.List;

public class KuisNoticeInfo extends KuisInfo {

    @JsonProperty("@d1#forum_id")
    protected String v1;

    @JsonProperty("@d1#subject_code")
    protected String v2;

    @JsonProperty("@d#")
    protected String v3 = "@d1#";

    @JsonProperty("@d1#")
    protected String v4 = "dmBoardNoticeParam";

    @JsonProperty("@d1#tp")
    protected String v5 = "dm";

    protected CategoryName categoryName;

    protected NoticeApiClient<CommonNoticeFormatDto, KuisNoticeInfo> noticeApiClient;

    public KuisNoticeInfo(KuisNoticeApiClient kuisNoticeApiClient) {
        this.noticeApiClient = kuisNoticeApiClient;
    }

    public List<CommonNoticeFormatDto> scrapLatestPageHtml() {
        return noticeApiClient.request(this);
    }

    public CategoryName getCategoryName() {
        return categoryName;
    }
}
