package com.kustacks.kuring.kuapi.deptinfo.engineering;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ChemicalDivisionDept extends EngineeringCollege {
    
    public ChemicalDivisionDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("18611422");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(1);
        boardSeqs.add("228");

        List<String> menuSeqs = new ArrayList<>(1);
        menuSeqs.add("1871");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "CHEMENG", boardSeqs, menuSeqs);
        this.code = "127111";
        this.deptName = "화학공학부";
    }
}
