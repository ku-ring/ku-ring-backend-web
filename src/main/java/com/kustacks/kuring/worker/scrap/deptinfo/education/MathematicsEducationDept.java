package com.kustacks.kuring.worker.scrap.deptinfo.education;

import com.kustacks.kuring.worker.parser.notice.LatestPageNoticeHtmlParser;
import com.kustacks.kuring.worker.scrap.client.notice.LatestPageNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.LatestPageNoticeProperties;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.List;

import static com.kustacks.kuring.notice.domain.DepartmentName.MATH_EDU;

@RegisterDepartmentMap(key = MATH_EDU)
public class MathematicsEducationDept extends EducationCollege {

    public MathematicsEducationDept(
            LatestPageNoticeApiClient latestPageNoticeApiClient,
            LatestPageNoticeHtmlParser latestPageNoticeHtmlParser,
            LatestPageNoticeProperties latestPageNoticeProperties
    ) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;
        this.latestPageNoticeProperties = latestPageNoticeProperties;

        List<Integer> siteIds = List.of(11345);
        this.staffScrapInfo = new StaffScrapInfo(MATH_EDU.getHostPrefix(), siteIds);
        this.noticeScrapInfo = new NoticeScrapInfo(MATH_EDU.getHostPrefix(), 499);
        this.departmentName = MATH_EDU;
    }
}
