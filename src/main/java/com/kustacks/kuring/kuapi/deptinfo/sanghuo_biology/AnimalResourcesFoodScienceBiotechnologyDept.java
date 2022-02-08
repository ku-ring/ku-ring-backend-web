package com.kustacks.kuring.kuapi.deptinfo.sanghuo_biology;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AnimalResourcesFoodScienceBiotechnologyDept extends SanghuoBiologyCollege {

    public AnimalResourcesFoodScienceBiotechnologyDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("15632573");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(1);
        boardSeqs.add("202");

        List<String> menuSeqs = new ArrayList<>(1);
        menuSeqs.add("1560");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "FOODBIO", boardSeqs, menuSeqs);
        this.code = "126909";
        this.deptName = "축산식품생명공학과";
    }
}
