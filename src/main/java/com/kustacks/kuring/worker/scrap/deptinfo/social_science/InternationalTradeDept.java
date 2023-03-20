package com.kustacks.kuring.worker.scrap.deptinfo.social_science;

import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class InternationalTradeDept extends SocialSciencesCollege {

    public InternationalTradeDept() {
        super();
        List<String> professorForumIds = List.of("15003249");;
        List<String> forumIds = List.of("9517");;
        List<String> boardSeqs = Collections.emptyList();
        List<String> menuSeqs = Collections.emptyList();

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "TRADE", boardSeqs, menuSeqs);
        this.code = "127123";
        this.deptName = "국제무역학과";
    }
}
