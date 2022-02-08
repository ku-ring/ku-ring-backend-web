package com.kustacks.kuring.kuapi.deptinfo.sanghuo_elective;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ElectiveEducationCenterDept extends SanghuoCollege {

    public ElectiveEducationCenterDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("12886454");

        List<String> forumIds = new ArrayList<>(1);
        forumIds.add("8281581");

        List<String> boardSeqs = new ArrayList<>(0);

        List<String> menuSeqs = new ArrayList<>(0);

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "ELECTIVEEDU", boardSeqs, menuSeqs);
        this.code = "126952";
        this.deptName = "교양교육센터";
    }
}
