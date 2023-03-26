package com.kustacks.kuring.worker.scrap.deptinfo.ku_integrated_science;

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

@RegisterDepartmentMap(key = DepartmentName.SMART_VEHICLE)
public class SmartVehicleDept extends KuIntegratedScienceCollege {

    public SmartVehicleDept(NoticeApiClient<ScrapingResultDto, DeptInfo> latestPageNoticeApiClient,
                            NoticeHtmlParser latestPageNoticeHtmlParser) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;

        List<String> professorForumIds = List.of("15883304");;
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("405");
        List<String> menuSeqs = List.of("2681");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "SMARTVEHICLE", boardSeqs, menuSeqs);
        this.code = "126914";
        this.departmentName = DepartmentName.SMART_VEHICLE;
    }
}
