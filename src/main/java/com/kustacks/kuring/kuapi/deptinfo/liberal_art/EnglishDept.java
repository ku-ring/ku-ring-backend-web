package com.kustacks.kuring.kuapi.deptinfo.liberal_art;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EnglishDept extends LiberalArtCollege {

    public EnglishDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("7603");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(1);
        boardSeqs.add("590");

        List<String> menuSeqs = new ArrayList<>(1);
        menuSeqs.add("4595");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "ENGLISH", boardSeqs, menuSeqs);
        this.code = "121254";
        this.deptName = "영어영문학과";
    }
}
