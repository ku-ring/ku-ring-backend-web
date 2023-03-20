package com.kustacks.kuring.worker.scrap.deptinfo.ku_integrated_science;

import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class SmartICTConvergenceDept extends KuIntegratedScienceCollege {

    public SmartICTConvergenceDept() {
        super();
        List<String> professorForumIds = List.of("15596417");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("401");
        List<String> menuSeqs = List.of("2632");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "SICTE", boardSeqs, menuSeqs);
        this.code = "126915";
        this.deptName = "스마트ICT융합공학과";
    }
}
