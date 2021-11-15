package com.kustacks.kuring.kuapi.scrap.deptinfo;

import lombok.Getter;

@Getter
public class StaffDeptInfo {

    private final String code;
    private final DeptUrl url;
    private final String deptName;
    private final String collegeName;

    public StaffDeptInfo(String code, String deptName, String collegeName, String ...pfForumIds) {

        this.code = code;
        this.deptName = deptName;
        this.collegeName = collegeName;
        url = new DeptUrl(deptName, pfForumIds);
    }

    @Override
    public String toString() {
        return deptName;
    }
}
