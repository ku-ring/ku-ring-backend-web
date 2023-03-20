package com.kustacks.kuring.worker.scrap.deptinfo.architecture;

import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ArchitectureDept extends ArchitectureCollege {

    public ArchitectureDept() {
        super();
        List<String> professorForumIds = List.of("11830", "17940");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("700");
        List<String> menuSeqs = List.of("5168");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "CAKU", boardSeqs, menuSeqs);
        this.code = "127320";
        this.deptName = "건축학부";
    }
}
