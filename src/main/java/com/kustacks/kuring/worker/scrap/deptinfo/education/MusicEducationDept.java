package com.kustacks.kuring.worker.scrap.deptinfo.education;

import com.kustacks.kuring.worker.dto.ScrapingResultDto;
import com.kustacks.kuring.worker.scrap.client.notice.NoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.LatestPageNoticeProperties;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import com.kustacks.kuring.worker.scrap.parser.notice.NoticeHtmlParserTemplate;

import java.util.List;

import static com.kustacks.kuring.notice.domain.DepartmentName.MUSIC_EDU;

@RegisterDepartmentMap(key = MUSIC_EDU)
public class MusicEducationDept extends EducationCollege {

    public MusicEducationDept(NoticeApiClient<ScrapingResultDto, DeptInfo> latestPageNoticeApiClient,
                              NoticeHtmlParserTemplate latestPageNoticeHtmlParser, LatestPageNoticeProperties latestPageNoticeProperties) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;
        this.latestPageNoticeProperties = latestPageNoticeProperties;

        List<String> professorForumIds = List.of("11383");
        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(MUSIC_EDU.getHostPrefix(), 503);
        this.departmentName = MUSIC_EDU;
    }
}
