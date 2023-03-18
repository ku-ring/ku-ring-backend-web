package com.kustacks.kuring.worker.update.staff.deptinfo;

import lombok.Getter;

@Getter
public class DeptInfo {

    private final String code;
    private final StaffScrapInfo staffScrapInfo;
    private final String deptName;
    private final String collegeName;

    public DeptInfo(String code, String deptName, String collegeName, String[] pfForumIds) {

        this.code = code;
        this.deptName = deptName;
        this.collegeName = collegeName;
        staffScrapInfo = new StaffScrapInfo(deptName, pfForumIds);
    }

    @Override
    public String toString() {
        return deptName;
    }
}
