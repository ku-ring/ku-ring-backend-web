package com.kustacks.kuring.worker.scrap.deptinfo.art_design;

import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.client.notice.NoticeApiClient;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import com.kustacks.kuring.worker.scrap.dto.ScrapingResultDto;
import com.kustacks.kuring.worker.scrap.parser.notice.NoticeHtmlParser;

import java.util.Collections;
import java.util.List;

// 교수 소개 구조 다른 홈페이지와 다름..
@RegisterDepartmentMap(key = DepartmentName.LIVING_DESIGN)
public class LivingDesignDept extends ArtDesignCollege {

    public LivingDesignDept(NoticeApiClient<ScrapingResultDto, DeptInfo> latestPageNoticeApiClient,
                            NoticeHtmlParser latestPageNoticeHtmlParser) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;

        List<String> professorForumIds = Collections.emptyList();
        List<String> forumIds = List.of("15382254");
        List<String> boardSeqs = List.of("1512");
        List<String> menuSeqs = List.of("11325");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "LIVINGDESIGN", boardSeqs, menuSeqs);
        this.code = "126781";
        this.departmentName = DepartmentName.LIVING_DESIGN;
    }
}
