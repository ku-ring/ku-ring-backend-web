package com.kustacks.kuring.worker.scrap.deptinfo;

import lombok.Getter;

import java.util.List;

@Getter
public class StaffScrapInfo extends ScrapInfo {

    private final List<String> professorForumId;

    public StaffScrapInfo(List<String> professorForumId) {
        this.professorForumId = professorForumId;
    }
}
