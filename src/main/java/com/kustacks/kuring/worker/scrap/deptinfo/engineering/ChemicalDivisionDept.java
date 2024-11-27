package com.kustacks.kuring.worker.scrap.deptinfo.engineering;

import com.kustacks.kuring.worker.parser.notice.LatestPageNoticeHtmlParser;
import com.kustacks.kuring.worker.scrap.client.notice.LatestPageNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.LatestPageNoticeProperties;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.List;

import static com.kustacks.kuring.notice.domain.DepartmentName.CHEMI_DIV;

@RegisterDepartmentMap(key = CHEMI_DIV)
public class ChemicalDivisionDept extends EngineeringCollege {
    
    public ChemicalDivisionDept(
            LatestPageNoticeApiClient latestPageNoticeApiClient,
            LatestPageNoticeHtmlParser latestPageNoticeHtmlParser,
            LatestPageNoticeProperties latestPageNoticeProperties
    ) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;
        this.latestPageNoticeProperties = latestPageNoticeProperties;

        List<Integer> siteIds = List.of(9926);
        this.staffScrapInfo = new StaffScrapInfo(CHEMI_DIV.getHostPrefix(), siteIds);
        this.noticeScrapInfo = new NoticeScrapInfo(CHEMI_DIV.getHostPrefix(), 409);
        this.departmentName = CHEMI_DIV;
    }
}
