package com.kustacks.kuring.worker.scrap.deptinfo;

import lombok.Getter;

@Getter
public class NoticeScrapInfo {

    private final String siteName;
    private final int siteId;

    public NoticeScrapInfo(String siteName, int siteId) {
        this.siteName = siteName;
        this.siteId = siteId;
    }
}
