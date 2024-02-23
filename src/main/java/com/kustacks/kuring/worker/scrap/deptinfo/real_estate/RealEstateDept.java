package com.kustacks.kuring.worker.scrap.deptinfo.real_estate;

import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.worker.scrap.client.notice.property.LatestPageNoticeProperties;
import com.kustacks.kuring.worker.scrap.client.notice.NoticeApiClient;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import com.kustacks.kuring.worker.dto.ScrapingResultDto;
import com.kustacks.kuring.worker.scrap.parser.notice.NoticeHtmlParserTemplate;

import java.util.Collections;
import java.util.List;

@RegisterDepartmentMap(key = DepartmentName.REAL_ESTATE)
public class RealEstateDept extends RealEstateCollege {

    // 부동산학과는 교수진 정보를 렌더링하는 방법이 다름. 따라서 pfForumId 인자를 전달하지 않았다.
    public RealEstateDept(NoticeApiClient<ScrapingResultDto, DeptInfo> realEstateNoticeApiClient,
                          NoticeHtmlParserTemplate realEstateNoticeHtmlParser, LatestPageNoticeProperties latestPageNoticeProperties) {
        super();
        this.noticeApiClient = realEstateNoticeApiClient;
        this.htmlParser = realEstateNoticeHtmlParser;
        this.latestPageNoticeProperties = latestPageNoticeProperties;

        List<String> professorForumIds = Collections.emptyList();
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = Collections.emptyList();
        List<String> menuSeqs = Collections.emptyList();

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "REALESTATE", boardSeqs, menuSeqs);
        this.code = "127426";
        this.departmentName = DepartmentName.REAL_ESTATE;
    }
}
