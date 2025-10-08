package com.kustacks.kuring.worker.scrap.deptinfo.social_science;

import com.kustacks.kuring.worker.parser.notice.LatestPageNoticeHtmlParser;
import com.kustacks.kuring.worker.scrap.client.notice.LatestPageGraduateNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.LatestPageNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.LatestPageNoticeProperties;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.List;

import static com.kustacks.kuring.notice.domain.DepartmentName.POLITICS;

@RegisterDepartmentMap(key = POLITICS)
public class PoliticalScienceDept extends SocialSciencesCollege {

    public PoliticalScienceDept(
            LatestPageNoticeApiClient latestPageNoticeApiClient,
            LatestPageNoticeHtmlParser latestPageNoticeHtmlParser,
            LatestPageNoticeProperties latestPageNoticeProperties,
            LatestPageGraduateNoticeApiClient latestPageGraduateNoticeApiClient
    ) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;
        this.latestPageNoticeProperties = latestPageNoticeProperties;

        List<Integer> siteIds = List.of(10199);
        this.staffScrapInfo = new StaffScrapInfo(POLITICS.getHostPrefix(), siteIds);
        this.noticeScrapInfo = new NoticeScrapInfo(POLITICS.getHostPrefix(), 803);
        this.departmentName = POLITICS;
        this.noticeGraduationInfo = new NoticeScrapInfo(POLITICS.getHostPrefix(), 421);
        this.latestPageGraduateNoticeApiClient = latestPageGraduateNoticeApiClient;
    }
}
