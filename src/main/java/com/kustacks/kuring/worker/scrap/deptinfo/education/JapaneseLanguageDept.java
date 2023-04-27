package com.kustacks.kuring.worker.scrap.deptinfo.education;

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

@RegisterDepartmentMap(key = DepartmentName.JAPANESE_EDU)
public class JapaneseLanguageDept extends EducationCollege {

    public JapaneseLanguageDept(NoticeApiClient<ScrapingResultDto, DeptInfo> latestPageNoticeApiClient,
                                NoticeHtmlParser latestPageNoticeHtmlParserTwo, LatestPageProperties latestPageProperties) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParserTwo;
        this.latestPageProperties = latestPageProperties;

        List<String> professorForumIds = List.of("12706");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("1092");
        List<String> menuSeqs = List.of("7643");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "JAPAN", boardSeqs, menuSeqs);
        this.code = "104981";
        this.departmentName = DepartmentName.JAPANESE_EDU;
    }
}
