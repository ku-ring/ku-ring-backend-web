package com.kustacks.kuring.worker.scrap.deptinfo.sanghuo_elective;

import com.kustacks.kuring.worker.parser.notice.LatestPageNoticeHtmlParser;
import com.kustacks.kuring.worker.scrap.client.notice.LatestPageNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.LatestPageNoticeProperties;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.Collections;
import java.util.List;

import static com.kustacks.kuring.notice.domain.DepartmentName.VOLUNTEER;

// TODO: 교직원 스크랩 시 장애가 되지 않는지 확인 필요
@RegisterDepartmentMap(key = VOLUNTEER)
public class VolunteerCenterDept extends SanghuoCollege {

    public VolunteerCenterDept(
            LatestPageNoticeApiClient latestPageNoticeApiClient,
            LatestPageNoticeHtmlParser latestPageNoticeHtmlParser,
            LatestPageNoticeProperties latestPageNoticeProperties
    ) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;
        this.latestPageNoticeProperties = latestPageNoticeProperties;

        List<String> professorForumIds = Collections.emptyList();
        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(VOLUNTEER.getHostPrefix(), 523);
        this.departmentName = VOLUNTEER;
    }
}
