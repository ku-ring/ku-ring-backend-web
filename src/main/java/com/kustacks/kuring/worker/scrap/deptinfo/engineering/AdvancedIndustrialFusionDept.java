package com.kustacks.kuring.worker.scrap.deptinfo.engineering;

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

@RegisterDepartmentMap(key = DepartmentName.ADV_INDUSTRIAL)
public class AdvancedIndustrialFusionDept extends EngineeringCollege {

    public AdvancedIndustrialFusionDept(NoticeApiClient<ScrapingResultDto, DeptInfo> latestPageNoticeApiClient,
                                        NoticeHtmlParser latestPageNoticeHtmlParserTwo, LatestPageProperties latestPageProperties) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParserTwo;
        this.latestPageProperties = latestPageProperties;

        List<String> professorForumIds = List.of("4113024");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("1109");
        List<String> menuSeqs = List.of("7794");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "AIF", boardSeqs, menuSeqs);
        this.code = "127431";
        this.departmentName = DepartmentName.ADV_INDUSTRIAL;
    }
}
