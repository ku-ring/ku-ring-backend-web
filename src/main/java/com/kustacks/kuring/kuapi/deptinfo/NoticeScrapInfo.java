package com.kustacks.kuring.kuapi.deptinfo;

import lombok.Getter;

import java.util.List;

@Getter
public class NoticeScrapInfo {

    private final List<String> forumIds;
    private final String siteId;
    private final List<String> boardSeqs;
    private final List<String> menuSeqs;

    public NoticeScrapInfo(List<String> forumIds, String siteId, List<String> boardSeqs, List<String> menuSeqs) {
        this.forumIds = forumIds;
        this.siteId = siteId;
        this.boardSeqs = boardSeqs;
        this.menuSeqs = menuSeqs;
    }
}
