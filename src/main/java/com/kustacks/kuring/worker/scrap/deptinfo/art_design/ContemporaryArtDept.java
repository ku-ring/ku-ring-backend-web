package com.kustacks.kuring.worker.scrap.deptinfo.art_design;

import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ContemporaryArtDept extends ArtDesignCollege {

    public ContemporaryArtDept() {
        super();
        List<String> professorForumIds = List.of("4089");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("986");
        List<String> menuSeqs = List.of("6805");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "CONTEMPORARYART", boardSeqs, menuSeqs);
        this.code = "122406";
        this.deptName = "현대미술학과";
    }
}
