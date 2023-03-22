package com.kustacks.kuring.worker.scrap.deptinfo.engineering;

import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.Collections;
import java.util.List;

@RegisterDepartmentMap(key = DepartmentName.INDUSTRIAL)
public class IndustrialDept extends EngineeringCollege {

    public IndustrialDept() {
        super();
        List<String> professorForumIds = List.of("4930");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("840");
        List<String> menuSeqs = List.of("5857");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "KIES", boardSeqs, menuSeqs);
        this.code = "127430";
        this.deptName = DepartmentName.INDUSTRIAL.getKorName();
    }
}
