package com.kustacks.kuring.worker.scrap.deptinfo.architecture;

import com.kustacks.kuring.worker.parser.notice.LatestPageNoticeHtmlParser;
import com.kustacks.kuring.worker.scrap.client.notice.LatestPageNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.LatestPageNoticeProperties;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.List;

import static com.kustacks.kuring.notice.domain.DepartmentName.ARCHITECTURE;

@RegisterDepartmentMap(key = ARCHITECTURE)
public class ArchitectureDept extends ArchitectureCollege {

    public ArchitectureDept(
            LatestPageNoticeApiClient latestPageNoticeApiClient,
            LatestPageNoticeHtmlParser latestPageNoticeHtmlParser,
            LatestPageNoticeProperties latestPageNoticeProperties
    ) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;
        this.latestPageNoticeProperties = latestPageNoticeProperties;

        List<Integer> siteIds = List.of(9824);
        this.staffScrapInfo = new StaffScrapInfo(ARCHITECTURE.getHostPrefix(), siteIds);
        this.noticeScrapInfo = new NoticeScrapInfo(ARCHITECTURE.getHostPrefix(), 397);
        this.departmentName = ARCHITECTURE;
    }
}
