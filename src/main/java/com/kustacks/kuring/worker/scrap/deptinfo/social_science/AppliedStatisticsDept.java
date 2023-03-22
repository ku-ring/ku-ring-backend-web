package com.kustacks.kuring.worker.scrap.deptinfo.social_science;

import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.Collections;
import java.util.List;

@RegisterDepartmentMap(key = DepartmentName.STATISTICS)
public class AppliedStatisticsDept extends SocialSciencesCollege {

    public AppliedStatisticsDept() {
        super();
        List<String> professorForumIds = List.of("5081");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("963");
        List<String> menuSeqs = List.of("6642");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "STAT", boardSeqs, menuSeqs);
        this.code = "127124";
        this.deptName = DepartmentName.STATISTICS.getKorName();
    }
}
