package com.kustacks.kuring.worker.scrap.deptinfo.real_estate;

import com.kustacks.kuring.worker.parser.notice.LatestPageNoticeHtmlParser;
import com.kustacks.kuring.worker.scrap.client.notice.LatestPageGraduateNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.LatestPageNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.LatestPageNoticeProperties;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.List;

import static com.kustacks.kuring.notice.domain.DepartmentName.REAL_ESTATE;

@RegisterDepartmentMap(key = REAL_ESTATE)
public class RealEstateDept extends RealEstateCollege {

    public RealEstateDept(
            LatestPageNoticeApiClient latestPageNoticeApiClient,
            LatestPageNoticeHtmlParser latestPageNoticeHtmlParser,
            LatestPageNoticeProperties latestPageNoticeProperties,
            LatestPageGraduateNoticeApiClient latestPageGraduateNoticeApiClient
    ) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;
        this.latestPageNoticeProperties = latestPageNoticeProperties;

        List<Integer> siteIds = List.of(13949);
        this.staffScrapInfo = new StaffScrapInfo(REAL_ESTATE.getHostPrefix(), siteIds);
        this.noticeScrapInfo = new NoticeScrapInfo(REAL_ESTATE.getHostPrefix(), 1563);
        this.departmentName = REAL_ESTATE;
        this.noticeGraduationInfo = new NoticeScrapInfo(REAL_ESTATE.getHostPrefix(), 1565);
        this.latestPageGraduateNoticeApiClient = latestPageGraduateNoticeApiClient;
    }
}
