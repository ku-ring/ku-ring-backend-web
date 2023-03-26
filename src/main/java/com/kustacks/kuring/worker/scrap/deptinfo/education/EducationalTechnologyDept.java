package com.kustacks.kuring.worker.scrap.deptinfo.education;

import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.client.notice.NoticeApiClient;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import com.kustacks.kuring.worker.scrap.dto.ScrapingResultDto;
import com.kustacks.kuring.worker.scrap.parser.notice.NoticeHtmlParser;

import java.util.List;

@RegisterDepartmentMap(key = DepartmentName.EDU_TECH)
public class EducationalTechnologyDept extends EducationCollege {

    public EducationalTechnologyDept(NoticeApiClient<ScrapingResultDto, DeptInfo> latestPageNoticeApiClient,
                                     NoticeHtmlParser latestPageNoticeHtmlParser) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;

        List<String> professorForumIds = List.of("5099763");
        List<String> forumIds = List.of("11707");
        List<String> boardSeqs = List.of("1462");
        List<String> menuSeqs = List.of("10740");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "EDUTECH", boardSeqs, menuSeqs);
        this.code = "105031";
        this.departmentName = DepartmentName.EDU_TECH;
    }
}
