package com.kustacks.kuring.kuapi.deptinfo.sanghuo_biology;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EnvironmentalHealthScienceDept extends SanghuoBiologyCollege {

    public EnvironmentalHealthScienceDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("13900359");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(1);
        boardSeqs.add("267");

        List<String> menuSeqs = new ArrayList<>(1);
        menuSeqs.add("2059");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "HEALTHENV", boardSeqs, menuSeqs);
        this.code = "126911";
        this.deptName = "환경보건과학과";
    }
}
