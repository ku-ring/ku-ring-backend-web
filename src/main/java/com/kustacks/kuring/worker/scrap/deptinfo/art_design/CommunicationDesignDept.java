package com.kustacks.kuring.worker.scrap.deptinfo.art_design;


import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class CommunicationDesignDept extends ArtDesignCollege {

    public CommunicationDesignDept() {
        super();
        List<String> professorForumIds = Collections.emptyList();
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = Collections.emptyList();
        List<String> menuSeqs = Collections.emptyList();

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "COMMDESIGN", boardSeqs, menuSeqs);
        this.code = "122402";
        this.deptName = "커뮤니케이션디자인학과";
    }
}
