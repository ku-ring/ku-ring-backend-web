package com.kustacks.kuring.worker.scrap.deptinfo.liberal_art;

import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class PhilosophyDept extends LiberalArtCollege {

    public PhilosophyDept() {
        super();
        List<String> professorForumIds = List.of("8564");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("1238");
        List<String> menuSeqs = List.of("8686");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "PHILO", boardSeqs, menuSeqs);
        this.code = "121256";
        this.deptName = "철학과";
    }
}
