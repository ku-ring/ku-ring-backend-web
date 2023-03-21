package com.kustacks.kuring.worker.scrap.deptinfo.engineering;

import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.Collections;
import java.util.List;

@RegisterDepartmentMap(key = DepartmentName.BIOLOGICAL)
public class BiologicalDept extends EngineeringCollege {

    public BiologicalDept() {
        super();
        List<String> professorForumIds = List.of("7849");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("1131");
        List<String> menuSeqs = List.of("7888");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "MICROBIO", boardSeqs, menuSeqs);
        this.code = "122055";
        this.deptName = "생물공학과";
    }
}
