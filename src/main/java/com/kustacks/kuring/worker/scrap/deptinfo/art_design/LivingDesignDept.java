package com.kustacks.kuring.worker.scrap.deptinfo.art_design;

import com.kustacks.kuring.worker.parser.notice.LatestPageNoticeHtmlParser;
import com.kustacks.kuring.worker.scrap.client.notice.LatestPageNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.LatestPageNoticeProperties;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.List;

import static com.kustacks.kuring.notice.domain.DepartmentName.LIVING_DESIGN;

// 교수 소개 구조 다른 홈페이지와 다름..
@RegisterDepartmentMap(key = LIVING_DESIGN)
public class LivingDesignDept extends ArtDesignCollege {

    public LivingDesignDept(
            LatestPageNoticeApiClient latestPageNoticeApiClient,
            LatestPageNoticeHtmlParser latestPageNoticeHtmlParser,
            LatestPageNoticeProperties latestPageNoticeProperties
    ) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;
        this.latestPageNoticeProperties = latestPageNoticeProperties;

        List<Integer> siteIds = List.of(11223);
        this.staffScrapInfo = new StaffScrapInfo(LIVING_DESIGN.getUrlPrefix(), siteIds);
        this.noticeScrapInfo = new NoticeScrapInfo(LIVING_DESIGN.getUrlPrefix(), 962);
        this.departmentName = LIVING_DESIGN;
    }
}
