package com.kustacks.kuring.worker.scrap.deptinfo.ku_integrated_science;

import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class SmartVehicleDept extends KuIntegratedScienceCollege {

    public SmartVehicleDept() {
        super();
        List<String> professorForumIds = List.of("15883304");;
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("405");
        List<String> menuSeqs = List.of("2681");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "SMARTVEHICLE", boardSeqs, menuSeqs);
        this.code = "126914";
        this.deptName = "스마트운행제공학과";
    }
}
