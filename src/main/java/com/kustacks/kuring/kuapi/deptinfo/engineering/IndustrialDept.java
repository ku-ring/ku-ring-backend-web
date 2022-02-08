package com.kustacks.kuring.kuapi.deptinfo.engineering;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class IndustrialDept extends EngineeringCollege {

    public IndustrialDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("4930");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(1);
        boardSeqs.add("840");

        List<String> menuSeqs = new ArrayList<>(1);
        menuSeqs.add("5857");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "KIES", boardSeqs, menuSeqs);
        this.code = "127430";
        this.deptName = "산업공학과";
    }
}
