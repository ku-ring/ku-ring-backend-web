package com.kustacks.kuring.worker.scrap.deptinfo.education;

import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class MathematicsEducationDept extends EducationCollege {

    public MathematicsEducationDept() {
        super();
        List<String> professorForumIds = List.of("4037");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("1105");
        List<String> menuSeqs = List.of("7747");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "MATHEDU", boardSeqs, menuSeqs);
        this.code = "104991";
        this.deptName = "수학교육과";
    }
}
