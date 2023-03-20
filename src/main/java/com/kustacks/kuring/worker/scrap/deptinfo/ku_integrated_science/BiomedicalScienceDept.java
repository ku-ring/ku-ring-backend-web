package com.kustacks.kuring.worker.scrap.deptinfo.ku_integrated_science;

import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class BiomedicalScienceDept extends KuIntegratedScienceCollege {

    public BiomedicalScienceDept() {
        super();
        List<String> professorForumIds = List.of("15602966");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("291", "292", "293");
        List<String> menuSeqs = List.of("2199", "2201", "2203");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "BMSE", boardSeqs, menuSeqs);
        this.code = "126918";
        this.deptName = "의생명공학과";
    }
}
