package com.kustacks.kuring.worker.scrap.deptinfo.engineering;

import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ElectricalElectronicsDept extends EngineeringCollege {

    public ElectricalElectronicsDept() {
        super();
        List<String> professorForumIds = List.of("18634838");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("424");
        List<String> menuSeqs = List.of("2837");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "EE", boardSeqs, menuSeqs);
        this.code = "127110";
        this.deptName = "전기전자공학부";
    }
}
