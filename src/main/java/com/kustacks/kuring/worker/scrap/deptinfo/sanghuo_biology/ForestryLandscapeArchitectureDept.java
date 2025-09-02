package com.kustacks.kuring.worker.scrap.deptinfo.sanghuo_biology;

import com.kustacks.kuring.worker.parser.notice.LatestPageNoticeHtmlParser;
import com.kustacks.kuring.worker.scrap.client.notice.LatestPageNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.LatestPageNoticeProperties;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.List;

import static com.kustacks.kuring.notice.domain.DepartmentName.FORESTRY_LANDSCAPE_ARCH;

@RegisterDepartmentMap(key = FORESTRY_LANDSCAPE_ARCH)
public class ForestryLandscapeArchitectureDept extends SanghuoBiologyCollege {

    public ForestryLandscapeArchitectureDept(
            LatestPageNoticeApiClient latestPageNoticeApiClient,
            LatestPageNoticeHtmlParser latestPageNoticeHtmlParser,
            LatestPageNoticeProperties latestPageNoticeProperties
    ) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;
        this.latestPageNoticeProperties = latestPageNoticeProperties;

        List<Integer> siteIds = List.of(11103);
        this.staffScrapInfo = new StaffScrapInfo(FORESTRY_LANDSCAPE_ARCH.getUrlPrefix(), siteIds);
        this.noticeScrapInfo = new NoticeScrapInfo(FORESTRY_LANDSCAPE_ARCH.getUrlPrefix(), 479);
        this.departmentName = FORESTRY_LANDSCAPE_ARCH;
    }
}
