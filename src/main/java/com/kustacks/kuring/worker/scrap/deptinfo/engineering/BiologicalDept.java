package com.kustacks.kuring.worker.scrap.deptinfo.engineering;

import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.scrap.client.notice.LatestPageProperties;
import com.kustacks.kuring.worker.scrap.client.notice.NoticeApiClient;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import com.kustacks.kuring.worker.scrap.dto.ScrapingResultDto;
import com.kustacks.kuring.worker.scrap.parser.notice.NoticeHtmlParser;

import java.util.Collections;
import java.util.List;

@RegisterDepartmentMap(key = DepartmentName.BIOLOGICAL)
public class BiologicalDept extends EngineeringCollege {

    public BiologicalDept(NoticeApiClient<ScrapingResultDto, DeptInfo> latestPageNoticeApiClient,
                          NoticeHtmlParser latestPageNoticeHtmlParser, LatestPageProperties latestPageProperties) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;
        this.latestPageProperties = latestPageProperties;

        List<String> professorForumIds = List.of("7849");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("1131");
        List<String> menuSeqs = List.of("7888");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "MICROBIO", boardSeqs, menuSeqs);
        this.code = "122055";
        this.departmentName = DepartmentName.BIOLOGICAL;
    }
}
