package com.kustacks.kuring.worker.scrap.deptinfo.sanghuo_biology;

import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class AnimalResourcesFoodScienceBiotechnologyDept extends SanghuoBiologyCollege {

    public AnimalResourcesFoodScienceBiotechnologyDept() {
        super();
        List<String> professorForumIds = List.of("15632573");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("202");
        List<String> menuSeqs = List.of("1560");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "FOODBIO", boardSeqs, menuSeqs);
        this.code = "126909";
        this.deptName = "축산식품생명공학과";
    }
}
