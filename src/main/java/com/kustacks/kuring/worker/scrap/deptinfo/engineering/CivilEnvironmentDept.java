package com.kustacks.kuring.worker.scrap.deptinfo.engineering;

import com.kustacks.kuring.worker.dto.ScrapingResultDto;
import com.kustacks.kuring.worker.scrap.client.notice.NoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.LatestPageNoticeProperties;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import com.kustacks.kuring.worker.scrap.parser.notice.NoticeHtmlParserTemplate;

import java.util.List;

import static com.kustacks.kuring.notice.domain.DepartmentName.CIVIL_ENV;

@RegisterDepartmentMap(key = CIVIL_ENV)
public class CivilEnvironmentDept extends EngineeringCollege {
    
    public CivilEnvironmentDept(NoticeApiClient<ScrapingResultDto, DeptInfo> latestPageNoticeApiClient,
                                NoticeHtmlParserTemplate latestPageNoticeHtmlParser, LatestPageNoticeProperties latestPageNoticeProperties) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;
        this.latestPageNoticeProperties = latestPageNoticeProperties;

        List<String> professorForumIds = List.of("10022");
        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(CIVIL_ENV.getHostPrefix(), 401);
        this.departmentName = CIVIL_ENV;
    }
}
