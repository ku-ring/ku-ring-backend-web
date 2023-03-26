package com.kustacks.kuring.worker.scrap.deptinfo.science;

import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.client.notice.NoticeApiClient;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import com.kustacks.kuring.worker.scrap.dto.ScrapingResultDto;
import com.kustacks.kuring.worker.scrap.parser.notice.NoticeHtmlParser;

import java.util.List;

@RegisterDepartmentMap(key = DepartmentName.PHYSICS)
public class PhysicsDept extends ScienceCollege {
    public PhysicsDept(NoticeApiClient<ScrapingResultDto, DeptInfo> latestPageNoticeApiClient,
                       NoticeHtmlParser latestPageNoticeHtmlParser) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;

        List<String> professorForumIds = List.of("8747");
        List<String> forumIds = List.of("8897");
        List<String> boardSeqs = List.of("1505");
        List<String> menuSeqs = List.of("11209");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "PHYSIC", boardSeqs, menuSeqs);
        this.code = "126783";
        this.departmentName = DepartmentName.PHYSICS;
    }
}
