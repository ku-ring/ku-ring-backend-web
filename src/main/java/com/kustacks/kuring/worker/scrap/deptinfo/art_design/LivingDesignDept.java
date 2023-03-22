package com.kustacks.kuring.worker.scrap.deptinfo.art_design;

import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.Collections;
import java.util.List;

// 교수 소개 구조 다른 홈페이지와 다름..
@RegisterDepartmentMap(key = DepartmentName.LIVING_DESIGN)
public class LivingDesignDept extends ArtDesignCollege {

    public LivingDesignDept() {
        super();
        List<String> professorForumIds = Collections.emptyList();
        List<String> forumIds = List.of("15382254");
        List<String> boardSeqs = Collections.emptyList();
        List<String> menuSeqs = Collections.emptyList();

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "LIVINGDESIGN", boardSeqs, menuSeqs);
        this.code = "126781";
        this.deptName = DepartmentName.LIVING_DESIGN.getKorName();
    }
}
