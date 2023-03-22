package com.kustacks.kuring.worker.scrap.deptinfo.ku_integrated_science;

import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.Collections;
import java.util.List;

@RegisterDepartmentMap(key = DepartmentName.COSMETICS)
public class CosmeticsDept extends KuIntegratedScienceCollege {

    public CosmeticsDept() {
        super();
        List<String> professorForumIds = List.of("15623085");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("297");
        List<String> menuSeqs = List.of("2251");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "COSMETICS", boardSeqs, menuSeqs);
        this.code = "126916";
        this.deptName = DepartmentName.COSMETICS.getKorName();
    }
}
