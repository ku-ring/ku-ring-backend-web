package com.kustacks.kuring.worker.scrap.deptinfo.sanghuo_biology;

import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class CropScienceDept extends SanghuoBiologyCollege {

    public CropScienceDept() {
        super();
        List<String> professorForumIds = List.of("15766997");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("190");
        List<String> menuSeqs = List.of("1454");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "CROPSCIENCE", boardSeqs, menuSeqs);
        this.code = "126908";
        this.deptName = "식량자원과학과";
    }
}
