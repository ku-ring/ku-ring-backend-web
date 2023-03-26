package com.kustacks.kuring.worker.scrap.deptinfo.education;

import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.client.notice.NoticeApiClient;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import com.kustacks.kuring.worker.scrap.dto.ScrapingResultDto;
import com.kustacks.kuring.worker.scrap.parser.notice.NoticeHtmlParser;

import java.util.List;

@RegisterDepartmentMap(key = DepartmentName.MUSIC_EDU)
public class MusicEducationDept extends EducationCollege {

    public MusicEducationDept(NoticeApiClient<ScrapingResultDto, DeptInfo> latestPageNoticeApiClient,
                              NoticeHtmlParser latestPageNoticeHtmlParser) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;

        List<String> professorForumIds = List.of("9803");
        List<String> forumIds = List.of("9801");
        List<String> boardSeqs = List.of("1621");
        List<String> menuSeqs = List.of("11972");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "MUSICEDU", boardSeqs, menuSeqs);
        this.code = "105011";
        this.deptName = DepartmentName.MUSIC_EDU.getKorName();
    }
}
