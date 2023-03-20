package com.kustacks.kuring.worker.scrap.deptinfo.ku_integrated_science;

import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class SystemBiotechnologyDept extends KuIntegratedScienceCollege {

    public SystemBiotechnologyDept() {
        super();
        List<String> professorForumIds = List.of("14795511");
        List<String> forumIds = List.of("14715702");
        List<String> boardSeqs = Collections.emptyList();
        List<String> menuSeqs = Collections.emptyList();

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "SYSTEMBIO", boardSeqs, menuSeqs);
        this.code = "126919";
        this.deptName = "시스템생명공학과";
    }
}
