package com.kustacks.kuring.worker.scrap.deptinfo.liberal_art;

import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class HistoryDept extends LiberalArtCollege {

    public HistoryDept() {
        super();
        List<String> professorForumIds = List.of("10839");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("1259");
        List<String> menuSeqs = List.of("8802");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "KHISTORY", boardSeqs, menuSeqs);
        this.code = "121257";
        this.deptName = "사학과";
    }
}
