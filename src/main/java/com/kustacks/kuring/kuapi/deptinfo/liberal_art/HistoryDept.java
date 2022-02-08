package com.kustacks.kuring.kuapi.deptinfo.liberal_art;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HistoryDept extends LiberalArtCollege {

    public HistoryDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("10839");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(1);
        boardSeqs.add("1259");

        List<String> menuSeqs = new ArrayList<>(1);
        menuSeqs.add("8802");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "KHISTORY", boardSeqs, menuSeqs);
        this.code = "121257";
        this.deptName = "사학과";
    }
}
