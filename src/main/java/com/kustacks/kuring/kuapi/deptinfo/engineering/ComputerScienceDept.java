package com.kustacks.kuring.kuapi.deptinfo.engineering;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ComputerScienceDept extends EngineeringCollege {

    public ComputerScienceDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("12351719");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(1);
        boardSeqs.add("882");

        List<String> menuSeqs = new ArrayList<>(1);
        menuSeqs.add("6097");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "CSE", boardSeqs, menuSeqs);
        this.code = "127428";
        this.deptName = "컴퓨터공학부";
    }
}
