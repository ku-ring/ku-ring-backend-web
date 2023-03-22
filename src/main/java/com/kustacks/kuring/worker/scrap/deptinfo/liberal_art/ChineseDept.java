package com.kustacks.kuring.worker.scrap.deptinfo.liberal_art;

import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.Collections;
import java.util.List;

@RegisterDepartmentMap(key = DepartmentName.CHINESE)
public class ChineseDept extends LiberalArtCollege {

    public ChineseDept() {
        super();
        List<String> professorForumIds = List.of("3086");;
        List<String> forumIds = List.of("5335");;
        List<String> boardSeqs = Collections.emptyList();
        List<String> menuSeqs = Collections.emptyList();

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "CHINESE", boardSeqs, menuSeqs);
        this.code = "121255";
        this.deptName = DepartmentName.CHINESE.getKorName();
    }
}
