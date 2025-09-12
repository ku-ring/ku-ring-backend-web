package com.kustacks.kuring.worker.scrap.deptinfo.education;

import com.kustacks.kuring.worker.parser.notice.LatestPageNoticeHtmlParser;
import com.kustacks.kuring.worker.scrap.client.notice.LatestPageNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.LatestPageNoticeProperties;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.List;

import static com.kustacks.kuring.notice.domain.DepartmentName.ENGLISH_EDU;

@RegisterDepartmentMap(key = ENGLISH_EDU)
public class EnglishEducationDept extends EducationCollege {

    public EnglishEducationDept(
            LatestPageNoticeApiClient latestPageNoticeApiClient,
            LatestPageNoticeHtmlParser latestPageNoticeHtmlParser,
            LatestPageNoticeProperties latestPageNoticeProperties
    ) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;
        this.latestPageNoticeProperties = latestPageNoticeProperties;

        List<Integer> siteIds = List.of(11425);
        this.staffScrapInfo = new StaffScrapInfo(ENGLISH_EDU.getHostPrefix(), siteIds);
        this.noticeScrapInfo = new NoticeScrapInfo(ENGLISH_EDU.getHostPrefix(), 505);
        this.departmentName = ENGLISH_EDU;
        this.noticeScrapInfo = new NoticeScrapInfo(ENGLISH_EDU.getHostPrefix(), 990);
    }
}
