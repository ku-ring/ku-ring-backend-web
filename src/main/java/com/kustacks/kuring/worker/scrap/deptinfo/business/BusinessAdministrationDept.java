package com.kustacks.kuring.worker.scrap.deptinfo.business;

import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.Collections;
import java.util.List;

@RegisterDepartmentMap(key = DepartmentName.BUIS_ADMIN)
public class BusinessAdministrationDept extends BusinessCollege {

    public BusinessAdministrationDept() {
        super();
        List<String> professorForumIds = List.of("3685475");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("460");
        List<String> menuSeqs = List.of("3243");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "BIZ", boardSeqs, menuSeqs);
        this.code = "126780";
        this.deptName = DepartmentName.BUIS_ADMIN.getKorName();
    }
}
