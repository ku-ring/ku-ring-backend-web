package com.kustacks.kuring.worker.scrap.deptinfo.engineering;

import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class MechanicalAerospaceDept extends EngineeringCollege {

    public MechanicalAerospaceDept() {
        super();
        List<String> professorForumIds = List.of("20565480");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("988");
        List<String> menuSeqs = List.of("6823");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "MAE", boardSeqs, menuSeqs);
        this.code = "127427";
        this.deptName = "기계항공공학부";
    }
}
