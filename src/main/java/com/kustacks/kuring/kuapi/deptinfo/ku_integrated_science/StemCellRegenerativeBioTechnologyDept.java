package com.kustacks.kuring.kuapi.deptinfo.ku_integrated_science;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StemCellRegenerativeBioTechnologyDept extends KuIntegratedScienceCollege {

    public StemCellRegenerativeBioTechnologyDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("14914654");

        List<String> forumIds = new ArrayList<>(2);
        forumIds.add("14901014");
        forumIds.add("14904868");

        List<String> boardSeqs = new ArrayList<>(0);

        List<String> menuSeqs = new ArrayList<>(0);

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "STEMREGENERATION", boardSeqs, menuSeqs);
        this.code = "126917";
        this.deptName = "줄기세포재생공학과";
    }
}
