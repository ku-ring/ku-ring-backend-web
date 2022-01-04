package com.kustacks.kuring.kuapi.staff.deptinfo.business;

import com.kustacks.kuring.kuapi.staff.deptinfo.DeptInfo;

public class BusinessCollege extends DeptInfo {

    public BusinessCollege(String code, String deptName, String... pfForumIds) {
        super(code, deptName, "경영대학", pfForumIds);
    }
}
