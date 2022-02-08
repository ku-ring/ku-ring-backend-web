package com.kustacks.kuring.kuapi.deptinfo.sanghuo_biology;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FoodMarketingSafetyDept extends SanghuoBiologyCollege {

    public FoodMarketingSafetyDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("15827578");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(1);
        boardSeqs.add("262");

        List<String> menuSeqs = new ArrayList<>(1);
        menuSeqs.add("2004");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "KUFSM", boardSeqs, menuSeqs);
        this.code = "126910";
        this.deptName = "식품유통공학과";
    }
}
