package com.kustacks.kuring.worker.staff.deptinfo.business;

import com.kustacks.kuring.worker.staff.deptinfo.DeptInfo;

public class BusinessCollege extends DeptInfo {

    public BusinessCollege(String code, String deptName, String... pfForumIds) {
        super(code, deptName, "경영대학", pfForumIds);
    }
}
