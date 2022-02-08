package com.kustacks.kuring.kuapi.deptinfo.ku_integrated_science;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SmartVehicleDept extends KuIntegratedScienceCollege {

    public SmartVehicleDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("15883304");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(1);
        boardSeqs.add("405");

        List<String> menuSeqs = new ArrayList<>(1);
        menuSeqs.add("2681");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "SMARTVEHICLE", boardSeqs, menuSeqs);
        this.code = "126914";
        this.deptName = "스마트운행제공학과";
    }
}
