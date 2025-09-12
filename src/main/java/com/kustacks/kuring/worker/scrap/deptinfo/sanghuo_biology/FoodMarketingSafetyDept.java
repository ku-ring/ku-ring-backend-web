package com.kustacks.kuring.worker.scrap.deptinfo.sanghuo_biology;

import com.kustacks.kuring.worker.parser.notice.LatestPageNoticeHtmlParser;
import com.kustacks.kuring.worker.scrap.client.notice.LatestPageNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.LatestPageNoticeProperties;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.List;

import static com.kustacks.kuring.notice.domain.DepartmentName.FOOD_MARKETING;

@RegisterDepartmentMap(key = FOOD_MARKETING)
public class FoodMarketingSafetyDept extends SanghuoBiologyCollege {

    public FoodMarketingSafetyDept(
            LatestPageNoticeApiClient latestPageNoticeApiClient,
            LatestPageNoticeHtmlParser latestPageNoticeHtmlParser,
            LatestPageNoticeProperties latestPageNoticeProperties
    ) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;
        this.latestPageNoticeProperties = latestPageNoticeProperties;

        List<Integer> siteIds = List.of(11029);
        this.staffScrapInfo = new StaffScrapInfo(FOOD_MARKETING.getHostPrefix(), siteIds);
        this.noticeScrapInfo = new NoticeScrapInfo(FOOD_MARKETING.getHostPrefix(), 929);
        this.departmentName = FOOD_MARKETING;
        this.noticeGraduationInfo = new NoticeScrapInfo(FOOD_MARKETING.getHostPrefix(), 475);
    }
}
