package com.kustacks.kuring.kuapi.deptinfo.ku_integrated_science;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EnergyDept extends KuIntegratedScienceCollege {

    public EnergyDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("15618550");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(1);
        boardSeqs.add("417");

        List<String> menuSeqs = new ArrayList<>(1);
        menuSeqs.add("2759");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "ENERGY", boardSeqs, menuSeqs);
        this.code = "126913";
        this.deptName = "미래에너지공학과";
    }
}
