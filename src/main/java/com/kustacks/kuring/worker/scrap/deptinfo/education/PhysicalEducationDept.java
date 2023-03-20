package com.kustacks.kuring.worker.scrap.deptinfo.education;

import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class PhysicalEducationDept extends EducationCollege {

    public PhysicalEducationDept() {
        super();
        List<String> professorForumIds = List.of("5703");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("1097");
        List<String> menuSeqs = List.of("7701");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "KUPE", boardSeqs, menuSeqs);
        this.code = "105001";
        this.deptName = "체육교육과";
    }
}
