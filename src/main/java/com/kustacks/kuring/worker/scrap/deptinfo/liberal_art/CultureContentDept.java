package com.kustacks.kuring.worker.scrap.deptinfo.liberal_art;

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

@RegisterDepartmentMap(key = DepartmentName.CULTURE_CONT)
public class CultureContentDept extends LiberalArtCollege {

    public CultureContentDept(NoticeApiClient<ScrapingResultDto, DeptInfo> latestPageNoticeApiClient,
                              NoticeHtmlParser latestPageNoticeHtmlParser, LatestPageProperties latestPageProperties) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;
        this.latestPageProperties = latestPageProperties;

        List<String> professorForumIds = List.of("89388", "3934522", "14064096");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("1060");
        List<String> menuSeqs = List.of("7489");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "CULTURECONTENTS", boardSeqs, menuSeqs);
        this.code = "121259";
        this.departmentName = DepartmentName.CULTURE_CONT;
    }
}
