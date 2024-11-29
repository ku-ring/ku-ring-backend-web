package com.kustacks.kuring.worker.scrap.deptinfo.real_estate;

import com.kustacks.kuring.worker.parser.notice.RealEstateNoticeHtmlParser;
import com.kustacks.kuring.worker.scrap.client.notice.RealEstateNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.LatestPageNoticeProperties;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.List;

import static com.kustacks.kuring.notice.domain.DepartmentName.REAL_ESTATE;

@RegisterDepartmentMap(key = REAL_ESTATE)
public class RealEstateDept extends RealEstateCollege {

    public RealEstateDept(RealEstateNoticeApiClient realEstateNoticeApiClient,
                          RealEstateNoticeHtmlParser realEstateNoticeHtmlParser,
                          LatestPageNoticeProperties latestPageNoticeProperties) {
        super();
        this.noticeApiClient = realEstateNoticeApiClient;
        this.htmlParser = realEstateNoticeHtmlParser;
        this.latestPageNoticeProperties = latestPageNoticeProperties;

        List<Integer> siteIds = List.of(13949);
        this.staffScrapInfo = new StaffScrapInfo("kure",siteIds);
        this.noticeScrapInfo = new NoticeScrapInfo(REAL_ESTATE.getHostPrefix(), 1563);
        this.departmentName = REAL_ESTATE;
    }
}
