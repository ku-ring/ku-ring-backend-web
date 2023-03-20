package com.kustacks.kuring.worker.scrap.deptinfo.science;

import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class PhysicsDept extends ScienceCollege {
    public PhysicsDept() {
        super();
        List<String> professorForumIds = List.of("8747");
        List<String> forumIds = List.of("8897");
        List<String> boardSeqs = Collections.emptyList();
        List<String> menuSeqs = Collections.emptyList();

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "PHYSIC", boardSeqs, menuSeqs);
        this.code = "126783";
        this.deptName = "물리학과";
    }
}
