package com.kustacks.kuring.kuapi.deptinfo;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class DeptInfo {

    protected String code;
    protected StaffScrapInfo staffScrapInfo;
    protected NoticeScrapInfo noticeScrapInfo;
    protected String deptName;
    protected String collegeName;

    public DeptInfo(String code, String deptName, String collegeName,
                    List<String> forumIds, String siteId, List<String> boardSeqs, List<String> menuSeqs,
                    List<String> pfForumIds) {

        this.code = code;
        this.deptName = deptName;
        this.collegeName = collegeName;
        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, siteId, boardSeqs, menuSeqs);
    }

    @Override
    public String toString() {
        return deptName;
    }
}
