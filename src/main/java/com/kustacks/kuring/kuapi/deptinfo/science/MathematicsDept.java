package com.kustacks.kuring.kuapi.deptinfo.science;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MathematicsDept extends ScienceCollege {

    public MathematicsDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("8663");

        List<String> forumIds = new ArrayList<>(1);
        forumIds.add("8652");

        List<String> boardSeqs = new ArrayList<>(0);

        List<String> menuSeqs = new ArrayList<>(0);

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "MATH", boardSeqs, menuSeqs);
        this.code = "121260";
        this.deptName = "수학과";
    }
}
