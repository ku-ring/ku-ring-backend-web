package com.kustacks.kuring.worker.scrap.deptinfo.social_science;

import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class PoliticalScienceDept extends SocialSciencesCollege {

    public PoliticalScienceDept() {
        super();
        List<String> professorForumIds = List.of("6884", "14286274");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("1138");
        List<String> menuSeqs = List.of("7929");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "POL", boardSeqs, menuSeqs);
        this.code = "127120";
        this.deptName = "정치외교학과";
    }
}
