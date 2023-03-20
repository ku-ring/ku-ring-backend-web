package com.kustacks.kuring.worker.scrap.deptinfo.ku_integrated_science;

import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class IntergrativeBioscienceBiotechnologyDept extends KuIntegratedScienceCollege {

    public IntergrativeBioscienceBiotechnologyDept() {
        super();
        List<String> professorForumIds = List.of("14570494");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("1154");
        List<String> menuSeqs = List.of("8002");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "IBB", boardSeqs, menuSeqs);
        this.code = "126920";
        this.deptName = "융합생명공학과";
    }
}
