package com.kustacks.kuring.worker.scrap.deptinfo.education;

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

@RegisterDepartmentMap(key = DepartmentName.EDUCATION)
public class EducationDept extends EducationCollege {

    public EducationDept(NoticeApiClient<ScrapingResultDto, DeptInfo> latestPageNoticeApiClient,
                         NoticeHtmlParser latestPageNoticeHtmlParser, LatestPageProperties latestPageProperties) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;
        this.latestPageProperties = latestPageProperties;

        List<String> professorForumIds = List.of("6238");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("452");
        List<String> menuSeqs = List.of("3147");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "GYOJIK", boardSeqs, menuSeqs);
        this.code = "105041";
        this.departmentName = DepartmentName.EDUCATION;
    }
}
