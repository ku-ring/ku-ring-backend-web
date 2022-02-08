package com.kustacks.kuring.kuapi.deptinfo.sanghuo_elective;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

// TODO: 교직원 스크랩 시 장애가 되지 않는지 확인 필요
@Component
public class VolunteerCenterDept extends SanghuoCollege {

    public VolunteerCenterDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(0);

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(1);
        boardSeqs.add("773");
        boardSeqs.add("774");

        List<String> menuSeqs = new ArrayList<>(1);
        menuSeqs.add("5528");
        menuSeqs.add("5530");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "VOLUNTEER", boardSeqs, menuSeqs);
        this.code = "127424";
        this.deptName = "사회봉사센터";
    }
}
