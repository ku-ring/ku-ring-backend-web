package com.kustacks.kuring.kuapi.deptinfo.liberal_art;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MediaCommunicationDept extends LiberalArtCollege {

    public MediaCommunicationDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("11939245");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(1);
        boardSeqs.add("1043");

        List<String> menuSeqs = new ArrayList<>(1);
        menuSeqs.add("7275");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "COMM", boardSeqs, menuSeqs);
        this.code = "122281";
        this.deptName = "미디어커뮤니케이션학과";
    }
}
