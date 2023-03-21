package com.kustacks.kuring.worker.scrap.deptinfo.business;

import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.Collections;
import java.util.List;

@RegisterDepartmentMap(key = DepartmentName.TECH_BUSI)
public class TechnologicalBusinessDept extends BusinessCollege {

    public TechnologicalBusinessDept() {
        super();
        List<String> professorForumIds = List.of("3511696");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("1168");
        List<String> menuSeqs = List.of("8081");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "MOT", boardSeqs, menuSeqs);
        this.code = "121174";
        this.deptName = "기술경영학과";
    }
}
