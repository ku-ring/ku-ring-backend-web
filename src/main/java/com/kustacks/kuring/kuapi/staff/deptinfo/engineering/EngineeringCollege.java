package com.kustacks.kuring.kuapi.staff.deptinfo.engineering;

import com.kustacks.kuring.kuapi.staff.deptinfo.DeptInfo;

public class EngineeringCollege extends DeptInfo {

    public EngineeringCollege(String code, String deptName, String... pfForumIds) {
        super(code, deptName, "공과대학", pfForumIds);
    }
}
