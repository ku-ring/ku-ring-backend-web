package com.kustacks.kuring.worker.scrap.deptinfo.real_estate;

import com.kustacks.kuring.worker.dto.ScrapingResultDto;
import com.kustacks.kuring.worker.scrap.client.notice.NoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.LatestPageNoticeProperties;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import com.kustacks.kuring.worker.parser.notice.NoticeHtmlParserTemplate;

import java.util.List;

import static com.kustacks.kuring.notice.domain.DepartmentName.REAL_ESTATE;

@RegisterDepartmentMap(key = REAL_ESTATE)
public class RealEstateDept extends RealEstateCollege {

    // 부동산학과는 교수진 정보를 렌더링하는 방법이 다름. 따라서 pfForumId 인자를 전달하지 않았다.
    public RealEstateDept(NoticeApiClient<ScrapingResultDto, DeptInfo> realEstateNoticeApiClient,
                          NoticeHtmlParserTemplate realEstateNoticeHtmlParser, LatestPageNoticeProperties latestPageNoticeProperties) {
        super();
        this.noticeApiClient = realEstateNoticeApiClient;
        this.htmlParser = realEstateNoticeHtmlParser;
        this.latestPageNoticeProperties = latestPageNoticeProperties;

        List<String> professorForumIds = List.of("13949");
        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(REAL_ESTATE.getHostPrefix(), 1563);
        this.departmentName = REAL_ESTATE;
    }
}
