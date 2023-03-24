package com.kustacks.kuring.worker.scrap.deptinfo.veterinary_medicine;

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

// 공지사항은 수의학과랑 동일하므로 여기에는 정보를 기입하지 않음
@RegisterDepartmentMap(key = DepartmentName.VET_PRE)
public class PreVeterinaryDept extends VeterinaryMedicineCollege {

    public PreVeterinaryDept(NoticeApiClient<ScrapingResultDto, DeptInfo> latestPageNoticeApiClient,
                             NoticeHtmlParser latestPageNoticeHtmlParser) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;

        List<String> professorForumIds = List.of("42372");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = Collections.emptyList();
        List<String> menuSeqs = Collections.emptyList();

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "VETERINARY", boardSeqs, menuSeqs);
        this.code = "105091";
        this.deptName = DepartmentName.VET_PRE.getKorName();
    }
}
