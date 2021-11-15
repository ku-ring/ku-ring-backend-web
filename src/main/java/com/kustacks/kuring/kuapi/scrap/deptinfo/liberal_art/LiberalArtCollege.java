package com.kustacks.kuring.kuapi.scrap.deptinfo.liberal_art;

import com.kustacks.kuring.kuapi.scrap.deptinfo.StaffDeptInfo;

public class LiberalArtCollege extends StaffDeptInfo {

    public LiberalArtCollege(String code, String deptName, String... pfForumIds) {
        super(code, deptName, "문과대학", pfForumIds);
    }
}
