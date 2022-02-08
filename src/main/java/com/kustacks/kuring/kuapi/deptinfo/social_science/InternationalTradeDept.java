package com.kustacks.kuring.kuapi.deptinfo.social_science;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InternationalTradeDept extends SocialSciencesCollege {

    public InternationalTradeDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("15003249");

        List<String> forumIds = new ArrayList<>(1);
        forumIds.add("9517");

        List<String> boardSeqs = new ArrayList<>(0);

        List<String> menuSeqs = new ArrayList<>(0);

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "TRADE", boardSeqs, menuSeqs);
        this.code = "127123";
        this.deptName = "국제무역학과";
    }
}
