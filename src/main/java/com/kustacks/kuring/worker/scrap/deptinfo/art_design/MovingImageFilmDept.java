package com.kustacks.kuring.worker.scrap.deptinfo.art_design;

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

@RegisterDepartmentMap(key = DepartmentName.MOV_IMAGE)
public class MovingImageFilmDept extends ArtDesignCollege {

    public MovingImageFilmDept(NoticeApiClient<ScrapingResultDto, DeptInfo> latestPageNoticeApiClient,
                               NoticeHtmlParser latestPageNoticeHtmlParser, LatestPageProperties latestPageProperties) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;
        this.latestPageProperties = latestPageProperties;

        List<String> professorForumIds = List.of("15032480");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("580");
        List<String> menuSeqs = List.of("4508");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "MOVINGIMAGES", boardSeqs, menuSeqs);
        this.code = "127128";
        this.departmentName = DepartmentName.MOV_IMAGE;
    }
}
