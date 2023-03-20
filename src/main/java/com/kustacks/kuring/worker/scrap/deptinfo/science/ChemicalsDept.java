package com.kustacks.kuring.worker.scrap.deptinfo.science;

import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ChemicalsDept extends ScienceCollege {
    public ChemicalsDept() {
        super();
        List<String> professorForumIds = List.of("8900");
        List<String> forumIds = List.of("8897");
        List<String> boardSeqs = Collections.emptyList();
        List<String> menuSeqs = Collections.emptyList();

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "CHEMI", boardSeqs, menuSeqs);
        this.code = "121261";
        this.deptName = "화학과";
    }
}
