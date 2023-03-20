package com.kustacks.kuring.worker.scrap.deptinfo.art_design;


import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ApparelDesignDept extends ArtDesignCollege {

    public ApparelDesignDept() {
        super();
        List<String> professorForumIds = List.of("9723");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("1007", "1009", "1010");
        List<String> menuSeqs = List.of("6987", "6991", "6993");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "APPAREL", boardSeqs, menuSeqs);
        this.code = "122404";
        this.deptName = "의상디자인학과";
    }
}
