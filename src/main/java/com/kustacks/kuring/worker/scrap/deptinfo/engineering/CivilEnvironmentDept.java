package com.kustacks.kuring.worker.scrap.deptinfo.engineering;

import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.Collections;
import java.util.List;

@RegisterDepartmentMap(key = DepartmentName.CIVIL_ENV)
public class CivilEnvironmentDept extends EngineeringCollege {
    
    public CivilEnvironmentDept() {
        super();
        List<String> professorForumIds = List.of("6308");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("1049");
        List<String> menuSeqs = List.of("7353");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "CIVILENV", boardSeqs, menuSeqs);
        this.code = "127108";
        this.deptName = DepartmentName.CIVIL_ENV.getKorName();
    }
}
