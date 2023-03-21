package com.kustacks.kuring.worker.scrap.deptinfo.real_estate;

import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.Collections;
import java.util.List;

@RegisterDepartmentMap(key = DepartmentName.REAL_ESTATE)
public class RealEstateDept extends RealEstateCollege {

    // 부동산학과는 교수진 정보를 렌더링하는 방법이 다름. 따라서 pfForumId 인자를 전달하지 않았다.
    public RealEstateDept() {
        super();
        List<String> professorForumIds = Collections.emptyList();
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = Collections.emptyList();
        List<String> menuSeqs = Collections.emptyList();

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "REALESTATE", boardSeqs, menuSeqs);
        this.code = "127426";
        this.deptName = "부동산학과";
    }
}
