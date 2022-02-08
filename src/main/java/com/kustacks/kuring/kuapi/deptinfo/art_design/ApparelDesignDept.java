package com.kustacks.kuring.kuapi.deptinfo.art_design;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ApparelDesignDept extends ArtDesignCollege {

    public ApparelDesignDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("9723");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(3);
        boardSeqs.add("1007");
        boardSeqs.add("1009");
        boardSeqs.add("1010");

        List<String> menuSeqs = new ArrayList<>(3);
        menuSeqs.add("6987");
        menuSeqs.add("6991");
        menuSeqs.add("6993");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "APPAREL", boardSeqs, menuSeqs);
        this.code = "122404";
        this.deptName = "의상디자인학과";
    }
}
