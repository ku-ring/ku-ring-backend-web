package com.kustacks.kuring.worker.scrap.deptinfo.liberal_art;

import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.Collections;
import java.util.List;

@RegisterDepartmentMap(key = DepartmentName.MEDIA_COMM)
public class MediaCommunicationDept extends LiberalArtCollege {

    public MediaCommunicationDept() {
        super();
        List<String> professorForumIds = List.of("11939245");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("1043");
        List<String> menuSeqs = List.of("7275");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "COMM", boardSeqs, menuSeqs);
        this.code = "122281";
        this.deptName = "미디어커뮤니케이션학과";
    }
}
