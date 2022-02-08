package com.kustacks.kuring.kuapi.deptinfo.veterinary_medicine;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

// 공지사항은 수의학과랑 동일하므로 여기에는 정보를 기입하지 않음
@Component
public class PreVeterinaryDept extends VeterinaryMedicineCollege {

    public PreVeterinaryDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("42372");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(0);

        List<String> menuSeqs = new ArrayList<>(0);

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "VETERINARY", boardSeqs, menuSeqs);
        this.code = "105091";
        this.deptName = "수의예과";
    }
}
