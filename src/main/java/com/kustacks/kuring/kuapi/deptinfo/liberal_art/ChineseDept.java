package com.kustacks.kuring.kuapi.deptinfo.liberal_art;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ChineseDept extends LiberalArtCollege {

    public ChineseDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("3086");

        List<String> forumIds = new ArrayList<>(1);
        forumIds.add("5335");

        List<String> boardSeqs = new ArrayList<>(0);

        List<String> menuSeqs = new ArrayList<>(0);

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "CHINESE", boardSeqs, menuSeqs);
        this.code = "121255";
        this.deptName = "중어중문학과";
    }
}
