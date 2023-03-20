package com.kustacks.kuring.worker.scrap.deptinfo;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class DeptInfo {

    protected String code;
    protected String deptName;
    protected String collegeName;
    protected StaffScrapInfo staffScrapInfo;
    protected NoticeScrapInfo noticeScrapInfo;

    public DeptInfo(String code, String deptName, String collegeName,
                    List<String> forumIds, String siteId, List<String> boardSeqs, List<String> menuSeqs,
                    List<String> professorForumIds) {

        this.code = code;
        this.deptName = deptName;
        this.collegeName = collegeName;
        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, siteId, boardSeqs, menuSeqs);
    }

    @Override
    public String toString() {
        return deptName;
    }
}
