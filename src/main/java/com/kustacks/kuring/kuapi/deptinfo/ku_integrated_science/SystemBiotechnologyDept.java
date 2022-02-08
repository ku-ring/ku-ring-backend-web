package com.kustacks.kuring.kuapi.deptinfo.ku_integrated_science;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SystemBiotechnologyDept extends KuIntegratedScienceCollege {

    public SystemBiotechnologyDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("14795511");

        List<String> forumIds = new ArrayList<>(1);
        forumIds.add("14715702");

        List<String> boardSeqs = new ArrayList<>(0);

        List<String> menuSeqs = new ArrayList<>(0);

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "SYSTEMBIO", boardSeqs, menuSeqs);
        this.code = "126919";
        this.deptName = "시스템생명공학과";
    }
}
