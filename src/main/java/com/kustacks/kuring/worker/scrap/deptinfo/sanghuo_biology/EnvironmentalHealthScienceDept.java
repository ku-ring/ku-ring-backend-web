package com.kustacks.kuring.worker.scrap.deptinfo.sanghuo_biology;

import com.kustacks.kuring.worker.dto.ScrapingResultDto;
import com.kustacks.kuring.worker.scrap.client.notice.NoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.LatestPageNoticeProperties;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import com.kustacks.kuring.worker.scrap.parser.notice.NoticeHtmlParserTemplate;

import java.util.List;

import static com.kustacks.kuring.notice.domain.DepartmentName.ENV_HEALTH_SCIENCE;

@RegisterDepartmentMap(key = ENV_HEALTH_SCIENCE)
public class EnvironmentalHealthScienceDept extends SanghuoBiologyCollege {

    public EnvironmentalHealthScienceDept(NoticeApiClient<ScrapingResultDto, DeptInfo> latestPageNoticeApiClient,
                                          NoticeHtmlParserTemplate latestPageNoticeHtmlParser, LatestPageNoticeProperties latestPageNoticeProperties) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;
        this.latestPageNoticeProperties = latestPageNoticeProperties;

        List<String> professorForumIds = List.of("11062");
        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(ENV_HEALTH_SCIENCE.getHostPrefix(), 477);
        this.departmentName = ENV_HEALTH_SCIENCE;
    }
}
