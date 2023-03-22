package com.kustacks.kuring.worker.scrap.deptinfo.education;

import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.Collections;
import java.util.List;

@RegisterDepartmentMap(key = DepartmentName.ENGLISH_EDU)
public class EnglishEducationDept extends EducationCollege {

    public EnglishEducationDept() {
        super();
        List<String> professorForumIds = List.of("12930");
        List<String> forumIds = List.of("12927");
        List<String> boardSeqs = Collections.emptyList();
        List<String> menuSeqs = Collections.emptyList();

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "ENGLISHEDU", boardSeqs, menuSeqs);
        this.code = "121175";
        this.deptName = DepartmentName.ENGLISH_EDU.getKorName();
    }
}
