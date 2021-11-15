package com.kustacks.kuring.kuapi.scrap.deptinfo.engineering;

import com.kustacks.kuring.kuapi.scrap.deptinfo.StaffDeptInfo;

public class EngineeringCollege extends StaffDeptInfo {

    public EngineeringCollege(String code, String deptName, String... pfForumIds) {
        super(code, deptName, "공과대학", pfForumIds);
    }
}
