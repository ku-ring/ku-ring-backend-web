package com.kustacks.kuring.kuapi.deptinfo.engineering;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class KBeautyIndustryFusionDept extends EngineeringCollege {
    
    public KBeautyIndustryFusionDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("17275625");

        List<String> forumIds = new ArrayList<>(0);
        forumIds.add("17258184");

        List<String> boardSeqs = new ArrayList<>(0);

        List<String> menuSeqs = new ArrayList<>(0);

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "KBEAUTY", boardSeqs, menuSeqs);
        this.code = "127432";
        this.deptName = "K뷰티산업융합학과";
    }
}
