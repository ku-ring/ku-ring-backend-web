package com.kustacks.kuring.kuapi.deptinfo.social_science;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AppliedStatisticsDept extends SocialSciencesCollege {

    public AppliedStatisticsDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("5081");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(1);
        boardSeqs.add("963");

        List<String> menuSeqs = new ArrayList<>(1);
        menuSeqs.add("6642");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "STAT", boardSeqs, menuSeqs);
        this.code = "127124";
        this.deptName = "응용통계학과";
    }
}
