package com.kustacks.kuring.kuapi.deptinfo.liberal_art;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CultureContentDept extends LiberalArtCollege {

    public CultureContentDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(3);
        pfForumIds.add("89388");
        pfForumIds.add("3934522");
        pfForumIds.add("14064096");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(1);
        boardSeqs.add("1060");

        List<String> menuSeqs = new ArrayList<>(1);
        menuSeqs.add("7489");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "CULTURECONTENTS", boardSeqs, menuSeqs);
        this.code = "121259";
        this.deptName = "문화콘텐츠학과";
    }
}
