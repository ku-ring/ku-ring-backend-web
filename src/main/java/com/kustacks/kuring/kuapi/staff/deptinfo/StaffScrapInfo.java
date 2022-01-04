package com.kustacks.kuring.kuapi.staff.deptinfo;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public class StaffScrapInfo extends ScrapInfo {

    private final List<String> pfForumId;

    public StaffScrapInfo(String deptName, String[] pfForumIds) {
        this.pfForumId = Arrays.asList(pfForumIds);
    }
}
