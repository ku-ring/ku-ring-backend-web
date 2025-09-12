package com.kustacks.kuring.worker.scrap.deptinfo.liberal_art;

import com.kustacks.kuring.worker.parser.notice.LatestPageNoticeHtmlParser;
import com.kustacks.kuring.worker.scrap.client.notice.LatestPageNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.LatestPageNoticeProperties;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.List;

import static com.kustacks.kuring.notice.domain.DepartmentName.CULTURE_CONT;

@RegisterDepartmentMap(key = CULTURE_CONT)
public class CultureContentDept extends LiberalArtCollege {

    public CultureContentDept(
            LatestPageNoticeApiClient latestPageNoticeApiClient,
            LatestPageNoticeHtmlParser latestPageNoticeHtmlParser,
            LatestPageNoticeProperties latestPageNoticeProperties
    ) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;
        this.latestPageNoticeProperties = latestPageNoticeProperties;

        List<Integer> siteIds = List.of(8001);
        this.staffScrapInfo = new StaffScrapInfo(CULTURE_CONT.getHostPrefix(), siteIds);
        this.noticeScrapInfo = new NoticeScrapInfo(CULTURE_CONT.getHostPrefix(), 661);
        this.departmentName = CULTURE_CONT;
        this.noticeGraduationInfo = new NoticeScrapInfo(CULTURE_CONT.getHostPrefix(), 379);
    }
}
