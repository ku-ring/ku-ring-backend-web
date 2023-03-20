package com.kustacks.kuring.worker.scrap.deptinfo.veterinary_medicine;

import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

// 공지사항은 수의학과랑 동일하므로 여기에는 정보를 기입하지 않음
@Component
public class PreVeterinaryDept extends VeterinaryMedicineCollege {

    public PreVeterinaryDept() {
        super();
        List<String> professorForumIds = List.of("42372");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = Collections.emptyList();
        List<String> menuSeqs = Collections.emptyList();

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "VETERINARY", boardSeqs, menuSeqs);
        this.code = "105091";
        this.deptName = "수의예과";
    }
}
