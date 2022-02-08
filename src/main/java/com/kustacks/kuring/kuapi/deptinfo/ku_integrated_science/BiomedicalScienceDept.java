package com.kustacks.kuring.kuapi.deptinfo.ku_integrated_science;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BiomedicalScienceDept extends KuIntegratedScienceCollege {

    public BiomedicalScienceDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("15602966");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(3);
        boardSeqs.add("291");
        boardSeqs.add("292");
        boardSeqs.add("293");

        List<String> menuSeqs = new ArrayList<>(3);
        menuSeqs.add("2199");
        menuSeqs.add("2201");
        menuSeqs.add("2203");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "BMSE", boardSeqs, menuSeqs);
        this.code = "126918";
        this.deptName = "의생명공학과";
    }
}
