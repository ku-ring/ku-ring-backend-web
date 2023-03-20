package com.kustacks.kuring.worker.scrap.deptinfo.sanghuo_elective;

import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ElectiveEducationCenterDept extends SanghuoCollege {

    public ElectiveEducationCenterDept() {
        super();
        List<String> professorForumIds = List.of("12886454");
        List<String> forumIds = List.of("8281581");
        List<String> boardSeqs = Collections.emptyList();
        List<String> menuSeqs = Collections.emptyList();

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "ELECTIVEEDU", boardSeqs, menuSeqs);
        this.code = "126952";
        this.deptName = "교양교육센터";
    }
}
