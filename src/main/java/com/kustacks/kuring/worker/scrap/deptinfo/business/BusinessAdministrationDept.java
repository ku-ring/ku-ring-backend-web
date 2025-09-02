package com.kustacks.kuring.worker.scrap.deptinfo.business;

import com.kustacks.kuring.worker.parser.notice.LatestPageNoticeHtmlParser;
import com.kustacks.kuring.worker.scrap.client.notice.LatestPageNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.LatestPageNoticeProperties;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.List;

import static com.kustacks.kuring.notice.domain.DepartmentName.BUIS_ADMIN;

@RegisterDepartmentMap(key = BUIS_ADMIN)
public class BusinessAdministrationDept extends BusinessCollege {

    public BusinessAdministrationDept(
            LatestPageNoticeApiClient latestPageNoticeApiClient,
            LatestPageNoticeHtmlParser latestPageNoticeHtmlParser,
            LatestPageNoticeProperties latestPageNoticeProperties
    ) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;
        this.latestPageNoticeProperties = latestPageNoticeProperties;

        List<Integer> siteIds = List.of(10513);
        this.staffScrapInfo = new StaffScrapInfo(BUIS_ADMIN.getUrlPrefix(), siteIds);
        this.noticeScrapInfo = new NoticeScrapInfo("kbs", 437);
        this.departmentName = BUIS_ADMIN;
    }
}
