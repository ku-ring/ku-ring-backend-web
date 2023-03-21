package com.kustacks.kuring.worker.scrap.deptinfo.ku_integrated_science;

import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.Collections;
import java.util.List;

@RegisterDepartmentMap(key = DepartmentName.STEM_REGEN)
public class StemCellRegenerativeBioTechnologyDept extends KuIntegratedScienceCollege {

    public StemCellRegenerativeBioTechnologyDept() {
        super();
        List<String> professorForumIds = List.of("14914654");
        List<String> forumIds = List.of("14901014", "14904868");
        List<String> boardSeqs = Collections.emptyList();
        List<String> menuSeqs = Collections.emptyList();

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "STEMREGENERATION", boardSeqs, menuSeqs);
        this.code = "126917";
        this.deptName = "줄기세포재생공학과";
    }
}
