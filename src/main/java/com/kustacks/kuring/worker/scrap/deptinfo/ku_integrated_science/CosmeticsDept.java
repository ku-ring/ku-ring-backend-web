package com.kustacks.kuring.worker.scrap.deptinfo.ku_integrated_science;

import com.kustacks.kuring.worker.parser.notice.LatestPageNoticeHtmlParser;
import com.kustacks.kuring.worker.scrap.client.notice.LatestPageGraduateNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.LatestPageNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.LatestPageNoticeProperties;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.List;

import static com.kustacks.kuring.notice.domain.DepartmentName.COSMETICS;

@RegisterDepartmentMap(key = COSMETICS)
public class CosmeticsDept extends KuIntegratedScienceCollege {

    public CosmeticsDept(
            LatestPageNoticeApiClient latestPageNoticeApiClient,
            LatestPageNoticeHtmlParser latestPageNoticeHtmlParser,
            LatestPageNoticeProperties latestPageNoticeProperties,
            LatestPageGraduateNoticeApiClient latestPageGraduateNoticeApiClient
    ) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;
        this.latestPageNoticeProperties = latestPageNoticeProperties;

        List<Integer> siteIds = List.of(15931);
        this.staffScrapInfo = new StaffScrapInfo(COSMETICS.getHostPrefix(), siteIds);
        this.noticeScrapInfo = new NoticeScrapInfo(COSMETICS.getHostPrefix(), 457);
        this.departmentName = COSMETICS;
        this.noticeGraduationInfo = new NoticeScrapInfo(COSMETICS.getHostPrefix(), 873);
        this.latestPageGraduateNoticeApiClient = latestPageGraduateNoticeApiClient;
    }
}
