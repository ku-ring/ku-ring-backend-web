package com.kustacks.kuring.worker.scrap.deptinfo.sanghuo_biology;

import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class FoodMarketingSafetyDept extends SanghuoBiologyCollege {

    public FoodMarketingSafetyDept() {
        super();
        List<String> professorForumIds = List.of("15827578");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("262");
        List<String> menuSeqs = List.of("2004");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "KUFSM", boardSeqs, menuSeqs);
        this.code = "126910";
        this.deptName = "식품유통공학과";
    }
}
