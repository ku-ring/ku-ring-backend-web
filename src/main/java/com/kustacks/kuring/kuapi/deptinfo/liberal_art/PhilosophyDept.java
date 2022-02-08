package com.kustacks.kuring.kuapi.deptinfo.liberal_art;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PhilosophyDept extends LiberalArtCollege {

    public PhilosophyDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("8564");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(1);
        boardSeqs.add("1238");

        List<String> menuSeqs = new ArrayList<>(1);
        menuSeqs.add("8686");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "PHILO", boardSeqs, menuSeqs);
        this.code = "121256";
        this.deptName = "철학과";
    }
}
