package com.kustacks.kuring.worker.scrap.deptinfo.ku_integrated_science;

import com.kustacks.kuring.worker.parser.notice.LatestPageNoticeHtmlParser;
import com.kustacks.kuring.worker.scrap.client.notice.LatestPageNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.LatestPageNoticeProperties;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.List;

import static com.kustacks.kuring.notice.domain.DepartmentName.SYSTEM_BIO_TECH;

@RegisterDepartmentMap(key = SYSTEM_BIO_TECH)
public class SystemBiotechnologyDept extends KuIntegratedScienceCollege {

    public SystemBiotechnologyDept(
            LatestPageNoticeApiClient latestPageNoticeApiClient,
            LatestPageNoticeHtmlParser latestPageNoticeHtmlParser,
            LatestPageNoticeProperties latestPageNoticeProperties
    ) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;
        this.latestPageNoticeProperties = latestPageNoticeProperties;

        List<Integer> siteIds = List.of(16019);
        this.staffScrapInfo = new StaffScrapInfo(SYSTEM_BIO_TECH.getUrlPrefix(), siteIds);
        this.noticeScrapInfo = new NoticeScrapInfo(SYSTEM_BIO_TECH.getUrlPrefix(), 887);
        this.departmentName = SYSTEM_BIO_TECH;
    }
}
