package com.kustacks.kuring.worker.scrap.deptinfo.social_science;

import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.Collections;
import java.util.List;

@RegisterDepartmentMap(key = DepartmentName.DISCI_STUDIES)
public class InterDisciplinaryStudiesDept extends SocialSciencesCollege {

    public InterDisciplinaryStudiesDept() {
        super();
        List<String> professorForumIds = List.of("3716919");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("175");
        List<String> menuSeqs = List.of("1294");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "DOLA", boardSeqs, menuSeqs);
        this.code = "127125";
        this.deptName = DepartmentName.DISCI_STUDIES.getKorName();
    }
}
