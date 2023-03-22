package com.kustacks.kuring.worker.scrap.deptinfo.education;

import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.Collections;
import java.util.List;

@RegisterDepartmentMap(key = DepartmentName.EDU_TECH)
public class EducationalTechnologyDept extends EducationCollege {

    public EducationalTechnologyDept() {
        super();
        List<String> professorForumIds = List.of("5099763");
        List<String> forumIds = List.of("11707");
        List<String> boardSeqs = Collections.emptyList();
        List<String> menuSeqs = Collections.emptyList();

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "EDUTECH", boardSeqs, menuSeqs);
        this.code = "105031";
        this.deptName = DepartmentName.EDU_TECH.getKorName();
    }
}
