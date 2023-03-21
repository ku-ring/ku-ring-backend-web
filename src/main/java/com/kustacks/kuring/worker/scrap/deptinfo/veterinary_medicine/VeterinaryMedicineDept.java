package com.kustacks.kuring.worker.scrap.deptinfo.veterinary_medicine;

import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.Collections;
import java.util.List;

@RegisterDepartmentMap(key = DepartmentName.VET_MEDICINE)
public class VeterinaryMedicineDept extends VeterinaryMedicineCollege {

    public VeterinaryMedicineDept() {
        super();
        List<String> professorForumIds = List.of("86647");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("475");
        List<String> menuSeqs = List.of("3427");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "VETERINARY", boardSeqs, menuSeqs);
        this.code = "105101";
        this.deptName = "수의학과";
    }
}
