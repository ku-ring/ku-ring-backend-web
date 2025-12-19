package com.kustacks.kuring.worker.scrap.deptinfo.engineering;

import com.kustacks.kuring.worker.parser.notice.LatestPageNoticeHtmlParser;
import com.kustacks.kuring.worker.scrap.client.notice.LatestPageGraduateNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.LatestPageNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.LatestPageNoticeProperties;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.List;

import static com.kustacks.kuring.notice.domain.DepartmentName.MECHA_ROBOT_AUTO;

@RegisterDepartmentMap(key = MECHA_ROBOT_AUTO)
public class MechaRobotAutoDept extends EngineeringCollege {

    public MechaRobotAutoDept(
            LatestPageNoticeApiClient latestPageNoticeApiClient,
            LatestPageNoticeHtmlParser latestPageNoticeHtmlParser,
            LatestPageNoticeProperties latestPageNoticeProperties,
            LatestPageGraduateNoticeApiClient latestPageGraduateNoticeApiClient
    ) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;
        this.latestPageNoticeProperties = latestPageNoticeProperties;

        List<Integer> siteIds = List.of(10048);
        this.staffScrapInfo = new StaffScrapInfo(MECHA_ROBOT_AUTO.getHostPrefix(), siteIds);
        this.noticeScrapInfo = new NoticeScrapInfo(MECHA_ROBOT_AUTO.getHostPrefix(), 403);
        this.departmentName = MECHA_ROBOT_AUTO;
        this.noticeGraduationInfo = new NoticeScrapInfo(MECHA_ROBOT_AUTO.getHostPrefix(), 763);
        this.latestPageGraduateNoticeApiClient = latestPageGraduateNoticeApiClient;
    }
}