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

@RegisterDepartmentMap(key = DepartmentName.CONT_ART)
public class ContemporaryArtDept extends ArtDesignCollege {

    public ContemporaryArtDept(NoticeApiClient<ScrapingResultDto, DeptInfo> latestPageNoticeApiClient,
                               NoticeHtmlParser latestPageNoticeHtmlParser, LatestPageProperties latestPageProperties) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;
        this.latestPageProperties = latestPageProperties;

        List<String> professorForumIds = List.of("4089");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("986");
        List<String> menuSeqs = List.of("6805");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "CONTEMPORARYART", boardSeqs, menuSeqs);
        this.code = "122406";
        this.departmentName = DepartmentName.CONT_ART;
    }
}
