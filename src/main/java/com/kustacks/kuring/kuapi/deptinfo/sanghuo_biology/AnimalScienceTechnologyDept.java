package com.kustacks.kuring.kuapi.deptinfo.sanghuo_biology;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AnimalScienceTechnologyDept extends SanghuoBiologyCollege {

    public AnimalScienceTechnologyDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("17673919");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(1);
        boardSeqs.add("82");

        List<String> menuSeqs = new ArrayList<>(1);
        menuSeqs.add("797");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "ANIS", boardSeqs, menuSeqs);
        this.code = "126907";
        this.deptName = "동물자원과학과";
    }
}
