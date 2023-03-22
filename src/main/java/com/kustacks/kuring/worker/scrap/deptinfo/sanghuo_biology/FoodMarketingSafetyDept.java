package com.kustacks.kuring.worker.scrap.deptinfo.sanghuo_biology;

import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.Collections;
import java.util.List;

@RegisterDepartmentMap(key = DepartmentName.FOOD_MARKETING)
public class FoodMarketingSafetyDept extends SanghuoBiologyCollege {

    public FoodMarketingSafetyDept() {
        super();
        List<String> professorForumIds = List.of("15827578");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("262");
        List<String> menuSeqs = List.of("2004");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "KUFSM", boardSeqs, menuSeqs);
        this.code = "126910";
        this.deptName = DepartmentName.FOOD_MARKETING.getKorName();
    }
}
