package com.kustacks.kuring.worker.scrap.deptinfo.sanghuo_elective;

import com.kustacks.kuring.worker.parser.notice.LatestPageNoticeHtmlParser;
import com.kustacks.kuring.worker.scrap.client.notice.LatestPageNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.LatestPageNoticeProperties;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.Collections;

import static com.kustacks.kuring.notice.domain.DepartmentName.LIBERAL_STUDIES;

@RegisterDepartmentMap(key = LIBERAL_STUDIES)
public class KULiberalStudiesDept extends SanghuoCollege {

    public KULiberalStudiesDept(
            LatestPageNoticeApiClient latestPageNoticeApiClient,
            LatestPageNoticeHtmlParser latestPageNoticeHtmlParser,
            LatestPageNoticeProperties latestPageNoticeProperties
    ) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;
        this.latestPageNoticeProperties = latestPageNoticeProperties;

        this.staffScrapInfo = new StaffScrapInfo(null, Collections.emptyList());
        this.noticeScrapInfo = new NoticeScrapInfo(LIBERAL_STUDIES.getHostPrefix(), 5876);
        this.departmentName = LIBERAL_STUDIES;
    }
}
