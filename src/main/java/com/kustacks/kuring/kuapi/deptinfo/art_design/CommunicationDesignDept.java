package com.kustacks.kuring.kuapi.deptinfo.art_design;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CommunicationDesignDept extends ArtDesignCollege {

    public CommunicationDesignDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(0);

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(0);

        List<String> menuSeqs = new ArrayList<>(0);

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "COMMDESIGN", boardSeqs, menuSeqs);
        this.code = "122402";
        this.deptName = "커뮤니케이션디자인학과";
    }
}
