package com.kustacks.kuring.worker.scrap.deptinfo.liberal_art;

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

@RegisterDepartmentMap(key = DepartmentName.MEDIA_COMM)
public class MediaCommunicationDept extends LiberalArtCollege {

    public MediaCommunicationDept(NoticeApiClient<ScrapingResultDto, DeptInfo> latestPageNoticeApiClient,
                                  NoticeHtmlParser latestPageNoticeHtmlParser) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;

        List<String> professorForumIds = List.of("11939245");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("1043");
        List<String> menuSeqs = List.of("7275");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "COMM", boardSeqs, menuSeqs);
        this.code = "122281";
        this.departmentName = DepartmentName.MEDIA_COMM;
    }
}
