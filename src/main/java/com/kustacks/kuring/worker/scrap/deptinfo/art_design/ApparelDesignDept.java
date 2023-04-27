package com.kustacks.kuring.worker.scrap.deptinfo.art_design;

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

@RegisterDepartmentMap(key = DepartmentName.APPAREL_DESIGN)
public class ApparelDesignDept extends ArtDesignCollege {

    public ApparelDesignDept(NoticeApiClient<ScrapingResultDto, DeptInfo> latestPageNoticeApiClient,
                             NoticeHtmlParser latestPageNoticeHtmlParser, LatestPageProperties latestPageProperties) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;
        this.latestPageProperties = latestPageProperties;

        List<String> professorForumIds = List.of("9723");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("1007");
        List<String> menuSeqs = List.of("6987");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "APPAREL", boardSeqs, menuSeqs);
        this.code = "122404";
        this.departmentName = DepartmentName.APPAREL_DESIGN;
    }
}
