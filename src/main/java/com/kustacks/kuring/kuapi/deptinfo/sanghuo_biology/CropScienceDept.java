package com.kustacks.kuring.kuapi.deptinfo.sanghuo_biology;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CropScienceDept extends SanghuoBiologyCollege {

    public CropScienceDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("15766997");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(1);
        boardSeqs.add("190");

        List<String> menuSeqs = new ArrayList<>(1);
        menuSeqs.add("1454");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "CROPSCIENCE", boardSeqs, menuSeqs);
        this.code = "126908";
        this.deptName = "식량자원과학과";
    }
}
