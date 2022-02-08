package com.kustacks.kuring.kuapi.deptinfo.social_science;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GlobalBusinessDept extends SocialSciencesCollege {

    public GlobalBusinessDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("7516");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(1);
        boardSeqs.add("1002");

        List<String> menuSeqs = new ArrayList<>(1);
        menuSeqs.add("7026");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "ITRADE", boardSeqs, menuSeqs);
        this.code = "127126";
        this.deptName = "글로벌비즈니스학과";
    }
}
