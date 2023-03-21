package com.kustacks.kuring.worker.scrap.deptinfo.liberal_art;

import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.Collections;
import java.util.List;

@RegisterDepartmentMap(key = DepartmentName.GEOLOGY)
public class GeologyDept extends LiberalArtCollege {

    public GeologyDept() {
        super();
        List<String> professorForumIds = List.of("7781");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("1259");
        List<String> menuSeqs = List.of("8802");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "KUGEO", boardSeqs, menuSeqs);
        this.code = "127107";
        this.deptName = "지리학과";
    }
}
