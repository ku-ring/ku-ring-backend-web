package com.kustacks.kuring.kuapi.deptinfo.engineering;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MechanicalAerospaceDept extends EngineeringCollege {

    public MechanicalAerospaceDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("20565480");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(1);
        boardSeqs.add("988");

        List<String> menuSeqs = new ArrayList<>(1);
        menuSeqs.add("6823");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "MAE", boardSeqs, menuSeqs);
        this.code = "127427";
        this.deptName = "기계항공공학부";
    }
}
