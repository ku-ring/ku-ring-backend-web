package com.kustacks.kuring.worker.scrap.deptinfo.education;

import com.kustacks.kuring.worker.parser.notice.LatestPageNoticeHtmlParser;
import com.kustacks.kuring.worker.scrap.client.notice.LatestPageGraduateNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.LatestPageNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.LatestPageNoticeProperties;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.List;

import static com.kustacks.kuring.notice.domain.DepartmentName.EDU_TECH;

@RegisterDepartmentMap(key = EDU_TECH)
public class EducationalTechnologyDept extends EducationCollege {

    public EducationalTechnologyDept(
            LatestPageNoticeApiClient latestPageNoticeApiClient,
            LatestPageNoticeHtmlParser latestPageNoticeHtmlParser,
            LatestPageNoticeProperties latestPageNoticeProperties,
            LatestPageGraduateNoticeApiClient latestPageGraduateNoticeApiClient
    ) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;
        this.latestPageNoticeProperties = latestPageNoticeProperties;

        List<Integer> siteIds = List.of(16532);
        this.staffScrapInfo = new StaffScrapInfo(EDU_TECH.getHostPrefix(), siteIds);
        this.noticeScrapInfo = new NoticeScrapInfo(EDU_TECH.getHostPrefix(), 4020);
        this.departmentName = EDU_TECH;
        this.noticeGraduationInfo = new NoticeScrapInfo(EDU_TECH.getHostPrefix(), 4092);
        this.latestPageGraduateNoticeApiClient = latestPageGraduateNoticeApiClient;
    }
}
