package com.kustacks.kuring.worker.scrap.deptinfo.ku_integrated_science;

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

@RegisterDepartmentMap(key = DepartmentName.SMART_ICT)
public class SmartIctConvergenceDept extends KuIntegratedScienceCollege {

    public SmartIctConvergenceDept(NoticeApiClient<ScrapingResultDto, DeptInfo> latestPageNoticeApiClient,
                                   NoticeHtmlParserTemplate latestPageNoticeHtmlParser, LatestPageNoticeProperties latestPageNoticeProperties) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;
        this.latestPageNoticeProperties = latestPageNoticeProperties;

        List<String> professorForumIds = List.of("15596417");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("401");
        List<String> menuSeqs = List.of("2632");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "SICTE", boardSeqs, menuSeqs);
        this.code = "126915";
        this.departmentName = DepartmentName.SMART_ICT;
    }
}
