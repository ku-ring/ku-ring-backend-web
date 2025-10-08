package com.kustacks.kuring.worker.scrap.deptinfo.liberal_art;

import com.kustacks.kuring.worker.parser.notice.LatestPageNoticeHtmlParser;
import com.kustacks.kuring.worker.scrap.client.notice.LatestPageGraduateNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.LatestPageNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.LatestPageNoticeProperties;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.List;

import static com.kustacks.kuring.notice.domain.DepartmentName.PHILOSOPHY;

@RegisterDepartmentMap(key = PHILOSOPHY)
public class PhilosophyDept extends LiberalArtCollege {

    public PhilosophyDept(
            LatestPageNoticeApiClient latestPageNoticeApiClient,
            LatestPageNoticeHtmlParser latestPageNoticeHtmlParser,
            LatestPageNoticeProperties latestPageNoticeProperties,
            LatestPageGraduateNoticeApiClient latestPageGraduateNoticeApiClient
    ) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;
        this.latestPageNoticeProperties = latestPageNoticeProperties;

        List<Integer> siteIds = List.of(4061);
        this.staffScrapInfo = new StaffScrapInfo(PHILOSOPHY.getHostPrefix(), siteIds);
        this.noticeScrapInfo = new NoticeScrapInfo(PHILOSOPHY.getHostPrefix(), 356);
        this.departmentName = PHILOSOPHY;
        this.noticeGraduationInfo = new NoticeScrapInfo(PHILOSOPHY.getHostPrefix(), 360);
        this.latestPageGraduateNoticeApiClient = latestPageGraduateNoticeApiClient;
    }
}
