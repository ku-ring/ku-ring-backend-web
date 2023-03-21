package com.kustacks.kuring.worker.scrap.deptinfo.sanghuo_elective;

import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.Collections;
import java.util.List;

// TODO: 교직원 스크랩 시 장애가 되지 않는지 확인 필요
@RegisterDepartmentMap(key = DepartmentName.VOLUNTEER)
public class VolunteerCenterDept extends SanghuoCollege {

    public VolunteerCenterDept() {
        super();
        List<String> professorForumIds = Collections.emptyList();
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("773", "774");
        List<String> menuSeqs = List.of("5528", "5530");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "VOLUNTEER", boardSeqs, menuSeqs);
        this.code = "127424";
        this.deptName = "사회봉사센터";
    }
}
