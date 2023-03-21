package com.kustacks.kuring.worker.scrap.deptinfo.sanghuo_biology;

import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.Collections;
import java.util.List;

@RegisterDepartmentMap(key = DepartmentName.ANIMAL_SCIENCE)
public class AnimalScienceTechnologyDept extends SanghuoBiologyCollege {

    public AnimalScienceTechnologyDept() {
        super();
        List<String> professorForumIds = List.of("17673919");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("82");
        List<String> menuSeqs = List.of("797");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "ANIS", boardSeqs, menuSeqs);
        this.code = "126907";
        this.deptName = "동물자원과학과";
    }
}
