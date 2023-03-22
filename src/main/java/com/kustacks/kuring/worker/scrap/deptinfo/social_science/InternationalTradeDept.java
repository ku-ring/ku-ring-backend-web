package com.kustacks.kuring.worker.scrap.deptinfo.social_science;

import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.Collections;
import java.util.List;

@RegisterDepartmentMap(key = DepartmentName.INT_TRADE)
public class InternationalTradeDept extends SocialSciencesCollege {

    public InternationalTradeDept() {
        super();
        List<String> professorForumIds = List.of("15003249");;
        List<String> forumIds = List.of("9517");;
        List<String> boardSeqs = Collections.emptyList();
        List<String> menuSeqs = Collections.emptyList();

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "TRADE", boardSeqs, menuSeqs);
        this.code = "127123";
        this.deptName = DepartmentName.INT_TRADE.getKorName();
    }
}
