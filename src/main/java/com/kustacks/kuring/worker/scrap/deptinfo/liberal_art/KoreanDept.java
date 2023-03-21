package com.kustacks.kuring.worker.scrap.deptinfo.liberal_art;

import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.Collections;
import java.util.List;

@RegisterDepartmentMap(key = DepartmentName.KOREAN)
public class KoreanDept extends LiberalArtCollege {

    public KoreanDept() {
        super();
        List<String> professorForumIds = List.of("31204");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("506");
        List<String> menuSeqs = List.of("3681");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "KOREA", boardSeqs, menuSeqs);
        this.code = "121253";
        this.deptName = "국어국문학과";
    }
}
