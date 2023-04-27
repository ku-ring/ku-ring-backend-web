package com.kustacks.kuring.worker.scrap.deptinfo.sanghuo_elective;

import com.kustacks.kuring.notice.domain.DepartmentName;
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

// TODO: 교직원 스크랩 시 장애가 되지 않는지 확인 필요
@RegisterDepartmentMap(key = DepartmentName.VOLUNTEER)
public class VolunteerCenterDept extends SanghuoCollege {

    public VolunteerCenterDept(NoticeApiClient<ScrapingResultDto, DeptInfo> latestPageNoticeApiClient,
                               NoticeHtmlParser latestPageNoticeHtmlParser, LatestPageProperties latestPageProperties) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;
        this.latestPageProperties = latestPageProperties;

        List<String> professorForumIds = Collections.emptyList();
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("773");
        List<String> menuSeqs = List.of("5528");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "VOLUNTEER", boardSeqs, menuSeqs);
        this.code = "127424";
        this.departmentName = DepartmentName.VOLUNTEER;
    }
}
