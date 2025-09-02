package com.kustacks.kuring.worker.scrap.deptinfo.sanghuo_elective;

import com.kustacks.kuring.worker.parser.notice.LatestPageNoticeHtmlParser;
import com.kustacks.kuring.worker.scrap.client.notice.LatestPageNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.LatestPageNoticeProperties;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.List;

import static com.kustacks.kuring.notice.domain.DepartmentName.ELE_EDU_CENTER;

@RegisterDepartmentMap(key = ELE_EDU_CENTER)
public class ElectiveEducationCenterDept extends SanghuoCollege {

    public ElectiveEducationCenterDept(
            LatestPageNoticeApiClient latestPageNoticeApiClient,
            LatestPageNoticeHtmlParser latestPageNoticeHtmlParser,
            LatestPageNoticeProperties latestPageNoticeProperties
    ) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;
        this.latestPageNoticeProperties = latestPageNoticeProperties;

        List<Integer> siteIds = List.of(11471);
        this.staffScrapInfo = new StaffScrapInfo(ELE_EDU_CENTER.getUrlPrefix(), siteIds);
        this.noticeScrapInfo = new NoticeScrapInfo(ELE_EDU_CENTER.getUrlPrefix(), 509);
        this.departmentName = ELE_EDU_CENTER;
    }
}
