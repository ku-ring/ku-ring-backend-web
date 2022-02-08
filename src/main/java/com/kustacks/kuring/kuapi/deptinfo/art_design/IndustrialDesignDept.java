package com.kustacks.kuring.kuapi.deptinfo.art_design;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class IndustrialDesignDept extends ArtDesignCollege {

    public IndustrialDesignDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("4085");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(1);
        boardSeqs.add("439");

        List<String> menuSeqs = new ArrayList<>(1);
        menuSeqs.add("3015");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "DESIGNID", boardSeqs, menuSeqs);
        this.code = "122403";
        this.deptName = "산업디자인학과";
    }
}
