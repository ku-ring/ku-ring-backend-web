package com.kustacks.kuring.kuapi.deptinfo.veterinary_medicine;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class VeterinaryMedicineDept extends VeterinaryMedicineCollege {

    public VeterinaryMedicineDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("86647");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(1);
        boardSeqs.add("475");

        List<String> menuSeqs = new ArrayList<>(1);
        menuSeqs.add("3427");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "VETERINARY", boardSeqs, menuSeqs);
        this.code = "105101";
        this.deptName = "수의학과";
    }
}
