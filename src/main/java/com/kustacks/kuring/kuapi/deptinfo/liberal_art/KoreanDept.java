package com.kustacks.kuring.kuapi.deptinfo.liberal_art;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class KoreanDept extends LiberalArtCollege {

    public KoreanDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("31204");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(1);
        boardSeqs.add("506");

        List<String> menuSeqs = new ArrayList<>(1);
        menuSeqs.add("3681");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "KOREA", boardSeqs, menuSeqs);
        this.code = "121253";
        this.deptName = "국어국문학과";
    }
}
