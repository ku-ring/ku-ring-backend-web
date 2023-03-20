package com.kustacks.kuring.worker.scrap.deptinfo.sanghuo_biology;

import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class EnvironmentalHealthScienceDept extends SanghuoBiologyCollege {

    public EnvironmentalHealthScienceDept() {
        super();
        List<String> professorForumIds = List.of("13900359");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("267");
        List<String> menuSeqs = List.of("2059");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "HEALTHENV", boardSeqs, menuSeqs);
        this.code = "126911";
        this.deptName = "환경보건과학과";
    }
}
