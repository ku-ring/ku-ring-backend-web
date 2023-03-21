package com.kustacks.kuring.worker.scrap.deptinfo.education;

import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.Collections;
import java.util.List;

@RegisterDepartmentMap(key = DepartmentName.MUSIC_EDU)
public class MusicEducationDept extends EducationCollege {

    public MusicEducationDept() {
        super();
        List<String> professorForumIds = List.of("9803");
        List<String> forumIds = List.of("9801");
        List<String> boardSeqs = Collections.emptyList();
        List<String> menuSeqs = Collections.emptyList();

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "MUSICEDU", boardSeqs, menuSeqs);
        this.code = "105011";
        this.deptName = "음악교육과";
    }
}
