package com.kustacks.kuring.kuapi.staff.deptinfo.science;

import com.kustacks.kuring.kuapi.staff.deptinfo.DeptInfo;

public class ScienceCollege extends DeptInfo {

    public ScienceCollege(String code, String deptName, String... pfForumIds) {
        super(code, deptName, "이과대학", pfForumIds);
    }
}
