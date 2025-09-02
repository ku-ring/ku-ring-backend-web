package com.kustacks.kuring.worker.scrap.graduatedeptinfo.engineering;

import com.kustacks.kuring.worker.parser.notice.LatestPageNoticeHtmlParser;
import com.kustacks.kuring.worker.scrap.client.notice.LatestPageNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.LatestPageNoticeProperties;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.engineering.EngineeringCollege;

import static com.kustacks.kuring.notice.domain.DepartmentName.COMPUTER_GRADUATE;

@RegisterDepartmentMap(key = COMPUTER_GRADUATE)
public class ComputerScienceGraduateDept extends EngineeringCollege {
    public ComputerScienceGraduateDept(
            LatestPageNoticeApiClient latestPageNoticeApiClient,
            LatestPageNoticeHtmlParser latestPageNoticeHtmlParser,
            LatestPageNoticeProperties latestPageNoticeProperties
    ) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;
        this.latestPageNoticeProperties = latestPageNoticeProperties;

        this.noticeScrapInfo = new NoticeScrapInfo(COMPUTER_GRADUATE.getHostPrefix(), 411);
        this.departmentName = COMPUTER_GRADUATE;
    }
}
