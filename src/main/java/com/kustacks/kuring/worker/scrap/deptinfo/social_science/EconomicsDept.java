package com.kustacks.kuring.worker.scrap.deptinfo.social_science;

import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.Collections;
import java.util.List;

@RegisterDepartmentMap(key = DepartmentName.ECONOMICS)
public class EconomicsDept extends SocialSciencesCollege {

    public EconomicsDept() {
        super();
        List<String> professorForumIds = List.of("9842");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("866");
        List<String> menuSeqs = List.of("5981");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "ECONOMIC", boardSeqs, menuSeqs);
        this.code = "127121";
        this.deptName = "경제학과";
    }
}
