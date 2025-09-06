package com.kustacks.kuring.worker.scrap.deptinfo.education;

import com.kustacks.kuring.worker.parser.notice.LatestPageNoticeHtmlParser;
import com.kustacks.kuring.worker.scrap.client.notice.LatestPageNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.LatestPageNoticeProperties;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.List;

import static com.kustacks.kuring.notice.domain.DepartmentName.PHY_EDU;

@RegisterDepartmentMap(key = PHY_EDU)
public class PhysicalEducationDept extends EducationCollege {

    public PhysicalEducationDept(
            LatestPageNoticeApiClient latestPageNoticeApiClient,
            LatestPageNoticeHtmlParser latestPageNoticeHtmlParser,
            LatestPageNoticeProperties latestPageNoticeProperties
    ) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;
        this.latestPageNoticeProperties = latestPageNoticeProperties;

        List<Integer> siteIds = List.of(11371);
        this.staffScrapInfo = new StaffScrapInfo(PHY_EDU.getHostPrefix(), siteIds);
        this.noticeScrapInfo = new NoticeScrapInfo(PHY_EDU.getHostPrefix(), 501);
        this.departmentName = PHY_EDU;
    }
}
