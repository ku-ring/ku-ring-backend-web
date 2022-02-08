package com.kustacks.kuring.kuapi.deptinfo.business;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BusinessAdministrationDept extends BusinessCollege {

    public BusinessAdministrationDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(2);
        pfForumIds.add("3685475");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(1);
        boardSeqs.add("460");

        List<String> menuSeqs = new ArrayList<>(1);
        menuSeqs.add("3243");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "BIZ", boardSeqs, menuSeqs);
        this.code = "126780";
        this.deptName = "경영학과";
    }
}
