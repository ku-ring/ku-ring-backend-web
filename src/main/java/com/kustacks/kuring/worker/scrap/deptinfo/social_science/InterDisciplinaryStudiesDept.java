package com.kustacks.kuring.worker.scrap.deptinfo.social_science;

import com.kustacks.kuring.worker.parser.notice.LatestPageNoticeHtmlParser;
import com.kustacks.kuring.worker.scrap.client.notice.LatestPageNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.LatestPageNoticeProperties;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.List;

import static com.kustacks.kuring.notice.domain.DepartmentName.DISCI_STUDIES;

@RegisterDepartmentMap(key = DISCI_STUDIES)
public class InterDisciplinaryStudiesDept extends SocialSciencesCollege {

    public InterDisciplinaryStudiesDept(
            LatestPageNoticeApiClient latestPageNoticeApiClient,
            LatestPageNoticeHtmlParser latestPageNoticeHtmlParser,
            LatestPageNoticeProperties latestPageNoticeProperties
    ) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;
        this.latestPageNoticeProperties = latestPageNoticeProperties;

        List<Integer> siteIds = List.of(10416);
        this.staffScrapInfo = new StaffScrapInfo(DISCI_STUDIES.getHostPrefix(), siteIds);
        this.noticeScrapInfo = new NoticeScrapInfo(DISCI_STUDIES.getHostPrefix(), 433);
        this.departmentName = DISCI_STUDIES;
    }
}
