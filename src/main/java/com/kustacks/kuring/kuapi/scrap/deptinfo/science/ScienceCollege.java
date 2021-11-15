package com.kustacks.kuring.kuapi.scrap.deptinfo.science;

import com.kustacks.kuring.kuapi.scrap.deptinfo.StaffDeptInfo;

public class ScienceCollege extends StaffDeptInfo {

    public ScienceCollege(String code, String deptName, String... pfForumIds) {
        super(code, deptName, "이과대학", pfForumIds);
    }
}
