package com.kustacks.kuring.worker.scrap.deptinfo.engineering;

import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class AdvancedIndustrialFusionDept extends EngineeringCollege {

    public AdvancedIndustrialFusionDept() {
        super();
        List<String> professorForumIds = List.of("4113024");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("1109");
        List<String> menuSeqs = List.of("7794");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "AIF", boardSeqs, menuSeqs);
        this.code = "127431";
        this.deptName = "신산업융합학과";
    }
}
