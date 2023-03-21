package com.kustacks.kuring.worker.scrap.deptinfo.ku_integrated_science;

import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.Collections;
import java.util.List;

@RegisterDepartmentMap(key = DepartmentName.ENERGY)
public class EnergyDept extends KuIntegratedScienceCollege {

    public EnergyDept() {
        super();
        List<String> professorForumIds = List.of("15618550");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("417");
        List<String> menuSeqs = List.of("2759");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "ENERGY", boardSeqs, menuSeqs);
        this.code = "126913";
        this.deptName = "미래에너지공학과";
    }
}
