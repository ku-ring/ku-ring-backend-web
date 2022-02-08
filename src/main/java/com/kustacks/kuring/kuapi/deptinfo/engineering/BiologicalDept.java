package com.kustacks.kuring.kuapi.deptinfo.engineering;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BiologicalDept extends EngineeringCollege {

    public BiologicalDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("7849");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(1);
        boardSeqs.add("1131");

        List<String> menuSeqs = new ArrayList<>(1);
        menuSeqs.add("7888");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "MICROBIO", boardSeqs, menuSeqs);
        this.code = "122055";
        this.deptName = "생물공학과";
    }
}
