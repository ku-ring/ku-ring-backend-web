package com.kustacks.kuring.kuapi.deptinfo.social_science;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PoliticalScienceDept extends SocialSciencesCollege {

    public PoliticalScienceDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(2);
        pfForumIds.add("6884");
        pfForumIds.add("14286274");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(1);
        boardSeqs.add("1138");

        List<String> menuSeqs = new ArrayList<>(1);
        menuSeqs.add("7929");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "POL", boardSeqs, menuSeqs);
        this.code = "127120";
        this.deptName = "정치외교학과";
    }
}
