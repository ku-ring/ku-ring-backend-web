package com.kustacks.kuring.kuapi.deptinfo.ku_integrated_science;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class IntergrativeBioscienceBiotechnologyDept extends KuIntegratedScienceCollege {

    public IntergrativeBioscienceBiotechnologyDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("14570494");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(1);
        boardSeqs.add("1154");

        List<String> menuSeqs = new ArrayList<>(1);
        menuSeqs.add("8002");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "IBB", boardSeqs, menuSeqs);
        this.code = "126920";
        this.deptName = "융합생명공학과";
    }
}
