package com.kustacks.kuring.worker.scrap.deptinfo.sanghuo_biology;

import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ForestryLandscapeArchitectureDept extends SanghuoBiologyCollege {

    public ForestryLandscapeArchitectureDept() {
        super();
        List<String> professorForumIds = List.of("15766919");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("413");
        List<String> menuSeqs = List.of("2729");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "FLA", boardSeqs, menuSeqs);
        this.code = "126912";
        this.deptName = "산림조경학과";
    }
}
