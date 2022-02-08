package com.kustacks.kuring.kuapi.deptinfo.art_design;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

// 교수 소개 구조 다른 홈페이지와 다름..
@Component
public class LivingDesignDept extends ArtDesignCollege {

    public LivingDesignDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(0);

        List<String> forumIds = new ArrayList<>(1);
        forumIds.add("15382254");

        List<String> boardSeqs = new ArrayList<>(0);

        List<String> menuSeqs = new ArrayList<>(0);

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "LIVINGDESIGN", boardSeqs, menuSeqs);
        this.code = "126781";
        this.deptName = "리빙디자인학과";
    }
}
