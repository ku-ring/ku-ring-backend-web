package com.kustacks.kuring.kuapi.deptinfo.business;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TechnologicalBusinessDept extends BusinessCollege {

    public TechnologicalBusinessDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(2);
        pfForumIds.add("3511696");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(1);
        boardSeqs.add("1168");

        List<String> menuSeqs = new ArrayList<>(1);
        menuSeqs.add("8081");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "MOT", boardSeqs, menuSeqs);
        this.code = "121174";
        this.deptName = "기술경영학과";
    }
}
