package com.kustacks.kuring.worker.scrap.deptinfo.liberal_art;

import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class CultureContentDept extends LiberalArtCollege {

    public CultureContentDept() {
        super();
        List<String> professorForumIds = List.of("89388", "3934522", "14064096");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("1060");
        List<String> menuSeqs = List.of("7489");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "CULTURECONTENTS", boardSeqs, menuSeqs);
        this.code = "121259";
        this.deptName = "문화콘텐츠학과";
    }
}
