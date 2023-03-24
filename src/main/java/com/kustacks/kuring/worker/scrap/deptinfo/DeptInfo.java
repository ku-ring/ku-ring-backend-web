package com.kustacks.kuring.worker.scrap.deptinfo;

import com.kustacks.kuring.worker.client.notice.NoticeApiClient;
import com.kustacks.kuring.worker.scrap.dto.ScrapingResultDto;
import com.kustacks.kuring.worker.scrap.parser.HtmlParser;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DeptInfo {

    protected String code;
    protected String deptName;
    protected String collegeName;
    protected StaffScrapInfo staffScrapInfo;
    protected NoticeScrapInfo noticeScrapInfo;
    protected NoticeApiClient<ScrapingResultDto, DeptInfo> noticeApiClient;
    protected HtmlParser htmlParser;

    @Override
    public String toString() {
        return deptName;
    }
}
