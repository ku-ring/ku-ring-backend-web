package com.kustacks.kuring.kuapi.deptinfo.architecture;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ArchitectureDept extends ArchitectureCollege {

    public ArchitectureDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(2);
        pfForumIds.add("11830");
        pfForumIds.add("17940");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(1);
        boardSeqs.add("700");

        List<String> menuSeqs = new ArrayList<>(1);
        menuSeqs.add("5168");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "CAKU", boardSeqs, menuSeqs);
        this.code = "127320";
        this.deptName = "건축학부";
    }
}
