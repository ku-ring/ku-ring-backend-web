package com.kustacks.kuring.worker.scrap.deptinfo.business;

import com.kustacks.kuring.worker.parser.notice.LatestPageNoticeHtmlParser;
import com.kustacks.kuring.worker.scrap.client.notice.LatestPageNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.LatestPageNoticeProperties;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.List;

import static com.kustacks.kuring.notice.domain.DepartmentName.TECH_BUSI;

@RegisterDepartmentMap(key = TECH_BUSI)
public class TechnologicalBusinessDept extends BusinessCollege {

    public TechnologicalBusinessDept(
            LatestPageNoticeApiClient latestPageNoticeApiClient,
            LatestPageNoticeHtmlParser latestPageNoticeHtmlParser,
            LatestPageNoticeProperties latestPageNoticeProperties
    ) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;
        this.latestPageNoticeProperties = latestPageNoticeProperties;

        List<Integer> siteIds = List.of(10540);
        this.staffScrapInfo = new StaffScrapInfo(TECH_BUSI.getHostPrefix(), siteIds);
        this.noticeScrapInfo = new NoticeScrapInfo(TECH_BUSI.getHostPrefix(), 445);
        this.departmentName = TECH_BUSI;
    }
}
