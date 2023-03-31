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

@RegisterDepartmentMap(key = DepartmentName.CHEMI_DIV)
public class ChemicalDivisionDept extends EngineeringCollege {
    
    public ChemicalDivisionDept(NoticeApiClient<ScrapingResultDto, DeptInfo> latestPageNoticeApiClient,
                                NoticeHtmlParser latestPageNoticeHtmlParser, LatestPageProperties latestPageProperties) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;
        this.latestPageProperties = latestPageProperties;

        List<String> professorForumIds = List.of("18611422");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("228");
        List<String> menuSeqs = List.of("1871");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "CHEMENG", boardSeqs, menuSeqs);
        this.code = "127111";
        this.departmentName = DepartmentName.CHEMI_DIV;
    }
}
