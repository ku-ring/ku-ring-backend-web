package com.kustacks.kuring.kuapi.deptinfo.engineering;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CivilEnvironmentDept extends EngineeringCollege {
    
    public CivilEnvironmentDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("6308");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(1);
        boardSeqs.add("1049");

        List<String> menuSeqs = new ArrayList<>(1);
        menuSeqs.add("7353");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "CIVILENV", boardSeqs, menuSeqs);
        this.code = "127108";
        this.deptName = "사회환경공학부";
    }
}
