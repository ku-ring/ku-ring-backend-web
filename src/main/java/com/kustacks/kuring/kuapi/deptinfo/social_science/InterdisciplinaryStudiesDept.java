package com.kustacks.kuring.kuapi.deptinfo.social_science;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InterdisciplinaryStudiesDept extends SocialSciencesCollege {

    public InterdisciplinaryStudiesDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("3716919");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(1);
        boardSeqs.add("175");

        List<String> menuSeqs = new ArrayList<>(1);
        menuSeqs.add("1294");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "DOLA", boardSeqs, menuSeqs);
        this.code = "127125";
        this.deptName = "융합인재학과";
    }
}
