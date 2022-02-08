package com.kustacks.kuring.kuapi.deptinfo.ku_integrated_science;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SmartICTConvergenceDept extends KuIntegratedScienceCollege {

    public SmartICTConvergenceDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("15596417");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(1);
        boardSeqs.add("401");

        List<String> menuSeqs = new ArrayList<>(1);
        menuSeqs.add("2632");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "SICTE", boardSeqs, menuSeqs);
        this.code = "126915";
        this.deptName = "스마트ICT융합공학과";
    }
}
