package com.kustacks.kuring.kuapi.deptinfo.real_estate;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RealEstateDept extends RealEstateCollege {

    // 부동산학과는 교수진 정보를 렌더링하는 방법이 다름. 따라서 pfForumId 인자를 전달하지 않았다.
    public RealEstateDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(0);

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(0);

        List<String> menuSeqs = new ArrayList<>(0);

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "REALESTATE", boardSeqs, menuSeqs);
        this.code = "127426";
        this.deptName = "부동산학과";
    }
}
