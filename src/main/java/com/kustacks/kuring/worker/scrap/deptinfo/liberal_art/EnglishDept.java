package com.kustacks.kuring.worker.scrap.deptinfo.liberal_art;

import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.Collections;
import java.util.List;

@RegisterDepartmentMap(key = DepartmentName.ENGLISH)
public class EnglishDept extends LiberalArtCollege {

    public EnglishDept() {
        super();
        List<String> professorForumIds = List.of("7603");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("590");
        List<String> menuSeqs = List.of("4595");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "ENGLISH", boardSeqs, menuSeqs);
        this.code = "121254";
        this.deptName = DepartmentName.ENGLISH.getKorName();
    }
}
