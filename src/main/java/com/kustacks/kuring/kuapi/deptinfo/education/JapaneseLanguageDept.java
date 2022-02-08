package com.kustacks.kuring.kuapi.deptinfo.education;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JapaneseLanguageDept extends EducationCollege {
    
    public JapaneseLanguageDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("12706");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(1);
        boardSeqs.add("1092");

        List<String> menuSeqs = new ArrayList<>(1);
        menuSeqs.add("7643");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "JAPAN", boardSeqs, menuSeqs);
        this.code = "104981";
        this.deptName = "일어교육과";
    }
}
