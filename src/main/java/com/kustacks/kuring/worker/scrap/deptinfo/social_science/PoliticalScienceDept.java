package com.kustacks.kuring.worker.scrap.deptinfo.social_science;

import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.client.notice.NoticeApiClient;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import com.kustacks.kuring.worker.scrap.dto.ScrapingResultDto;
import com.kustacks.kuring.worker.scrap.parser.notice.NoticeHtmlParser;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@RegisterDepartmentMap(key = DepartmentName.POLITICS)
public class PoliticalScienceDept extends SocialSciencesCollege {

    public PoliticalScienceDept(NoticeApiClient<ScrapingResultDto, DeptInfo> latestPageNoticeApiClient,
                                NoticeHtmlParser latestPageNoticeHtmlParser) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;

        List<String> professorForumIds = List.of("6884", "14286274");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("1138");
        List<String> menuSeqs = List.of("7929");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "POL", boardSeqs, menuSeqs);
        this.code = "127120";
        this.departmentName = DepartmentName.POLITICS;
    }
}
