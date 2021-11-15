package com.kustacks.kuring.kuapi.scrap.deptinfo.education;

import com.kustacks.kuring.kuapi.scrap.deptinfo.StaffDeptInfo;

public class EducationCollege extends StaffDeptInfo {

    public EducationCollege(String code, String deptName, String... pfForumIds) {
        super(code, deptName, "사범대학", pfForumIds);
    }
}
