package com.kustacks.kuring.worker.scrap.deptinfo.art_design;

import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.Collections;
import java.util.List;

@RegisterDepartmentMap(key = DepartmentName.IND_DESIGN)
public class IndustrialDesignDept extends ArtDesignCollege {

    public IndustrialDesignDept() {
        super();
        List<String> professorForumIds = List.of("4085");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("439");
        List<String> menuSeqs = List.of("3015");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "DESIGNID", boardSeqs, menuSeqs);
        this.code = "122403";
        this.deptName = DepartmentName.IND_DESIGN.getKorName();
    }
}
