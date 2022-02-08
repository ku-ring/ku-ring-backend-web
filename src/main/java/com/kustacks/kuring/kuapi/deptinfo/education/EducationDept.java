package com.kustacks.kuring.kuapi.deptinfo.education;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EducationDept extends EducationCollege {

    public EducationDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("6238");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(1);
        boardSeqs.add("452");

        List<String> menuSeqs = new ArrayList<>(1);
        menuSeqs.add("7701");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "GYOJIK", boardSeqs, menuSeqs);
        this.code = "105041";
        this.deptName = "교직과";
    }
}
