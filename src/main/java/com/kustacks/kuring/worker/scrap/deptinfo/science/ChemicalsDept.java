package com.kustacks.kuring.worker.scrap.deptinfo.science;

import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.worker.scrap.client.notice.property.LatestPageNoticeProperties;
import com.kustacks.kuring.worker.scrap.client.notice.NoticeApiClient;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import com.kustacks.kuring.worker.scrap.dto.ScrapingResultDto;
import com.kustacks.kuring.worker.scrap.parser.notice.NoticeHtmlParser;

import java.util.List;

@RegisterDepartmentMap(key = DepartmentName.CHEMICALS)
public class ChemicalsDept extends ScienceCollege {
    public ChemicalsDept(NoticeApiClient<ScrapingResultDto, DeptInfo> latestPageNoticeApiClient,
                         NoticeHtmlParser latestPageNoticeHtmlParserTwo, LatestPageNoticeProperties latestPageNoticeProperties) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParserTwo;
        this.latestPageNoticeProperties = latestPageNoticeProperties;

        List<String> professorForumIds = List.of("8900");
        List<String> forumIds = List.of("8897");
        List<String> boardSeqs = List.of("1525");
        List<String> menuSeqs = List.of("11371");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "CHEMI", boardSeqs, menuSeqs);
        this.code = "121261";
        this.departmentName = DepartmentName.CHEMICALS;
    }
}
