package com.kustacks.kuring.worker.scrap.deptinfo.education;

import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.worker.scrap.client.notice.property.LatestPageNoticeProperties;
import com.kustacks.kuring.worker.scrap.client.notice.NoticeApiClient;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import com.kustacks.kuring.worker.dto.ScrapingResultDto;
import com.kustacks.kuring.worker.scrap.parser.notice.NoticeHtmlParserTemplate;

import java.util.List;

@RegisterDepartmentMap(key = DepartmentName.MUSIC_EDU)
public class MusicEducationDept extends EducationCollege {

    public MusicEducationDept(NoticeApiClient<ScrapingResultDto, DeptInfo> latestPageNoticeApiClient,
                              NoticeHtmlParserTemplate latestPageNoticeHtmlParserTwo, LatestPageNoticeProperties latestPageNoticeProperties) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParserTwo;
        this.latestPageNoticeProperties = latestPageNoticeProperties;

        List<String> professorForumIds = List.of("9803");
        List<String> forumIds = List.of("9801");
        List<String> boardSeqs = List.of("1621");
        List<String> menuSeqs = List.of("11972");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "MUSIC", boardSeqs, menuSeqs);
        this.code = "105011";
        this.departmentName = DepartmentName.MUSIC_EDU;
    }
}
