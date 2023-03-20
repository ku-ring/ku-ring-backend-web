package com.kustacks.kuring.worker.scrap.deptinfo.engineering;

import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ComputerScienceDept extends EngineeringCollege {

    public ComputerScienceDept() {
        super();
        List<String> professorForumIds = List.of("12351719");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("882");
        List<String> menuSeqs = List.of("6097");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "CSE", boardSeqs, menuSeqs);
        this.code = "127428";
        this.deptName = "컴퓨터공학부";
    }
}
