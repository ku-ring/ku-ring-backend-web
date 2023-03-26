package com.kustacks.kuring.worker.scrap.deptinfo.engineering;

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

@RegisterDepartmentMap(key = DepartmentName.MECH_AERO)
public class MechanicalAerospaceDept extends EngineeringCollege {

    public MechanicalAerospaceDept(NoticeApiClient<ScrapingResultDto, DeptInfo> latestPageNoticeApiClient,
                                   NoticeHtmlParser latestPageNoticeHtmlParser) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;

        List<String> professorForumIds = List.of("20565480");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("988");
        List<String> menuSeqs = List.of("6823");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "MAE", boardSeqs, menuSeqs);
        this.code = "127427";
        this.departmentName = DepartmentName.MECH_AERO;
    }
}
