package com.kustacks.kuring.worker.scrap.deptinfo.education;

import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.Collections;
import java.util.List;

@RegisterDepartmentMap(key = DepartmentName.JAPANESE_EDU)
public class JapaneseLanguageDept extends EducationCollege {

    public JapaneseLanguageDept() {
        super();
        List<String> professorForumIds = List.of("12706");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("1092");
        List<String> menuSeqs = List.of("7643");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "JAPAN", boardSeqs, menuSeqs);
        this.code = "104981";
        this.deptName = DepartmentName.JAPANESE_EDU.getKorName();
    }
}
