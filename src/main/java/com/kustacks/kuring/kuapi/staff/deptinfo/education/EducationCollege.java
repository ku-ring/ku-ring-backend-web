package com.kustacks.kuring.kuapi.staff.deptinfo.education;

import com.kustacks.kuring.kuapi.staff.deptinfo.DeptInfo;

public class EducationCollege extends DeptInfo {

    public EducationCollege(String code, String deptName, String... pfForumIds) {
        super(code, deptName, "사범대학", pfForumIds);
    }
}
