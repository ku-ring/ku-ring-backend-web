package com.kustacks.kuring.kuapi.deptinfo.social_science;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PublicAdministrationDept extends SocialSciencesCollege {

    public PublicAdministrationDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("7245");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(1);
        boardSeqs.add("1145");

        List<String> menuSeqs = new ArrayList<>(1);
        menuSeqs.add("7970");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "KKUPA", boardSeqs, menuSeqs);
        this.code = "127122";
        this.deptName = "행정학과";
    }
}
