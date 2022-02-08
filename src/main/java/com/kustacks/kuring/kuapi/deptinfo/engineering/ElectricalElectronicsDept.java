package com.kustacks.kuring.kuapi.deptinfo.engineering;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ElectricalElectronicsDept extends EngineeringCollege {

    public ElectricalElectronicsDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("18634838");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(1);
        boardSeqs.add("424");

        List<String> menuSeqs = new ArrayList<>(1);
        menuSeqs.add("2837");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "EE", boardSeqs, menuSeqs);
        this.code = "127110";
        this.deptName = "전기전자공학부";
    }
}
