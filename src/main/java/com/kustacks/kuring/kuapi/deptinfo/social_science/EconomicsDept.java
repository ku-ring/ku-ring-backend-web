package com.kustacks.kuring.kuapi.deptinfo.social_science;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EconomicsDept extends SocialSciencesCollege {

    public EconomicsDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("9842");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(1);
        boardSeqs.add("866");

        List<String> menuSeqs = new ArrayList<>(1);
        menuSeqs.add("5981");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "ECONOMIC", boardSeqs, menuSeqs);
        this.code = "127121";
        this.deptName = "경제학과";
    }
}
