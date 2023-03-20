package com.kustacks.kuring.worker.scrap.deptinfo.engineering;

import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ChemicalDivisionDept extends EngineeringCollege {
    
    public ChemicalDivisionDept() {
        super();
        List<String> professorForumIds = List.of("18611422");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("228");
        List<String> menuSeqs = List.of("1871");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "CHEMENG", boardSeqs, menuSeqs);
        this.code = "127111";
        this.deptName = "화학공학부";
    }
}
