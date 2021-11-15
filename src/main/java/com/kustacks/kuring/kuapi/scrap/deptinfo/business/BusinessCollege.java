package com.kustacks.kuring.kuapi.scrap.deptinfo.business;

import com.kustacks.kuring.kuapi.scrap.deptinfo.StaffDeptInfo;

public class BusinessCollege extends StaffDeptInfo {

    public BusinessCollege(String code, String deptName, String... pfForumIds) {
        super(code, deptName, "경영대학", pfForumIds);
    }
}
