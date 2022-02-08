package com.kustacks.kuring.kuapi.deptinfo.art_design;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ContemporaryArtDept extends ArtDesignCollege {

    public ContemporaryArtDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("4089");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(1);
        boardSeqs.add("986");

        List<String> menuSeqs = new ArrayList<>(1);
        menuSeqs.add("6805");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "CONTEMPORARYART", boardSeqs, menuSeqs);
        this.code = "122406";
        this.deptName = "현대미술학과";
    }
}
