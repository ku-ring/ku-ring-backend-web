package com.kustacks.kuring.kuapi.deptinfo.education;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EnglishEducationDept extends EducationCollege {

    public EnglishEducationDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("12930");

        List<String> forumIds = new ArrayList<>(1);
        forumIds.add("12927");

        List<String> boardSeqs = new ArrayList<>(0);

        List<String> menuSeqs = new ArrayList<>(0);

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "ENGLISHEDU", boardSeqs, menuSeqs);
        this.code = "121175";
        this.deptName = "영어교육과";
    }
}
