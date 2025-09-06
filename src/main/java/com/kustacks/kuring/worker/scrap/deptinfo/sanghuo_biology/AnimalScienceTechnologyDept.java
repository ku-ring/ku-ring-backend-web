package com.kustacks.kuring.worker.scrap.deptinfo.sanghuo_biology;

import com.kustacks.kuring.worker.parser.notice.LatestPageNoticeHtmlParser;
import com.kustacks.kuring.worker.scrap.client.notice.LatestPageNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.LatestPageNoticeProperties;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.List;

import static com.kustacks.kuring.notice.domain.DepartmentName.ANIMAL_SCIENCE;

@RegisterDepartmentMap(key = ANIMAL_SCIENCE)
public class AnimalScienceTechnologyDept extends SanghuoBiologyCollege {

    public AnimalScienceTechnologyDept(
            LatestPageNoticeApiClient latestPageNoticeApiClient,
            LatestPageNoticeHtmlParser latestPageNoticeHtmlParser,
            LatestPageNoticeProperties latestPageNoticeProperties
    ) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;
        this.latestPageNoticeProperties = latestPageNoticeProperties;

        List<Integer> siteIds = List.of(10902);
        this.staffScrapInfo = new StaffScrapInfo(ANIMAL_SCIENCE.getHostPrefix(), siteIds);
        this.noticeScrapInfo = new NoticeScrapInfo(ANIMAL_SCIENCE.getHostPrefix(), 914);
        this.departmentName = ANIMAL_SCIENCE;
    }
}
