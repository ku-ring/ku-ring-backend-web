package com.kustacks.kuring.worker.scrap.deptinfo.ku_integrated_science;

import com.kustacks.kuring.worker.parser.notice.LatestPageNoticeHtmlParser;
import com.kustacks.kuring.worker.scrap.client.notice.LatestPageNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.LatestPageNoticeProperties;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.List;

import static com.kustacks.kuring.notice.domain.DepartmentName.INT_BIO_TECH;

@RegisterDepartmentMap(key = INT_BIO_TECH)
public class IntergrativeBioscienceBiotechnologyDept extends KuIntegratedScienceCollege {

    public IntergrativeBioscienceBiotechnologyDept(
            LatestPageNoticeApiClient latestPageNoticeApiClient,
            LatestPageNoticeHtmlParser latestPageNoticeHtmlParser,
            LatestPageNoticeProperties latestPageNoticeProperties
    ) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;
        this.latestPageNoticeProperties = latestPageNoticeProperties;

        List<Integer> siteIds = List.of(10851);
        this.staffScrapInfo = new StaffScrapInfo(INT_BIO_TECH.getHostPrefix(), siteIds);
        this.noticeScrapInfo = new NoticeScrapInfo(INT_BIO_TECH.getHostPrefix(), 895);
        this.departmentName = INT_BIO_TECH;
    }
}
