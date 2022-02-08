package com.kustacks.kuring.kuapi.deptinfo.ku_integrated_science;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CosmeticsDept extends KuIntegratedScienceCollege {

    public CosmeticsDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("15623085");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(1);
        boardSeqs.add("297");

        List<String> menuSeqs = new ArrayList<>(1);
        menuSeqs.add("2251");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "COSMETICS", boardSeqs, menuSeqs);
        this.code = "126916";
        this.deptName = "화장품공학과";
    }
}
