package com.kustacks.kuring.worker.scrap.deptinfo;

import lombok.Getter;

import java.util.List;

@Getter
public class StaffScrapInfo extends ScrapInfo {

    private final String siteName;
    private final List<Integer> siteIds;

    public StaffScrapInfo(String siteName, List<Integer> siteIds) {
        this.siteName = siteName;
        this.siteIds = siteIds;
    }
}
