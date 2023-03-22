package com.kustacks.kuring.worker.scrap.deptinfo.social_science;

import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.Collections;
import java.util.List;

@RegisterDepartmentMap(key = DepartmentName.ADMINISTRATION)
public class PublicAdministrationDept extends SocialSciencesCollege {

    public PublicAdministrationDept() {
        super();
        List<String> professorForumIds = List.of("7245");;
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("1145");;
        List<String> menuSeqs = List.of("7970");;

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "KKUPA", boardSeqs, menuSeqs);
        this.code = "127122";
        this.deptName = DepartmentName.ADMINISTRATION.getKorName();
    }
}
