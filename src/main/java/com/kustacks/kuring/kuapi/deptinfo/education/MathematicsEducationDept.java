package com.kustacks.kuring.kuapi.deptinfo.education;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MathematicsEducationDept extends EducationCollege {

    public MathematicsEducationDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("4037");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(1);
        boardSeqs.add("1105");

        List<String> menuSeqs = new ArrayList<>(1);
        menuSeqs.add("7747");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "MATHEDU", boardSeqs, menuSeqs);
        this.code = "104991";
        this.deptName = "수학교육과";
    }
}
