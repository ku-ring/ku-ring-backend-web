package com.kustacks.kuring.worker.scrap.deptinfo.education;

import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.Collections;
import java.util.List;

@RegisterDepartmentMap(key = DepartmentName.EDUCATION)
public class EducationDept extends EducationCollege {

    public EducationDept() {
        super();
        List<String> professorForumIds = List.of("6238");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("452");
        List<String> menuSeqs = List.of("7701");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "GYOJIK", boardSeqs, menuSeqs);
        this.code = "105041";
        this.deptName = DepartmentName.EDUCATION.getKorName();
    }
}
