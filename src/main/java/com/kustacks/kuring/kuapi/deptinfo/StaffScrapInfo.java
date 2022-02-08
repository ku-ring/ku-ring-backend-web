package com.kustacks.kuring.kuapi.deptinfo;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public class StaffScrapInfo extends ScrapInfo {

    private final List<String> pfForumId;

    public StaffScrapInfo(List<String> pfForumIds) {
        this.pfForumId = pfForumIds;
    }
}
