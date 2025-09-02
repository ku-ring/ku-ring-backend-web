package com.kustacks.kuring.worker.scrap.deptinfo.social_science;

import com.kustacks.kuring.worker.parser.notice.LatestPageNoticeHtmlParser;
import com.kustacks.kuring.worker.scrap.client.notice.LatestPageNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.LatestPageNoticeProperties;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.List;

import static com.kustacks.kuring.notice.domain.DepartmentName.INT_TRADE;

@RegisterDepartmentMap(key = INT_TRADE)
public class InternationalTradeDept extends SocialSciencesCollege {

    public InternationalTradeDept(
            LatestPageNoticeApiClient latestPageNoticeApiClient,
            LatestPageNoticeHtmlParser latestPageNoticeHtmlParser,
            LatestPageNoticeProperties latestPageNoticeProperties
    ) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;
        this.latestPageNoticeProperties = latestPageNoticeProperties;

        List<Integer> siteIds = List.of(10371);
        this.staffScrapInfo = new StaffScrapInfo(INT_TRADE.getUrlPrefix(), siteIds);
        this.noticeScrapInfo = new NoticeScrapInfo(INT_TRADE.getUrlPrefix(), 429);
        this.departmentName = INT_TRADE;
    }
}
