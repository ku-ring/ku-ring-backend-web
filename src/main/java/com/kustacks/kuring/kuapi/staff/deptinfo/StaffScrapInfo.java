package com.kustacks.kuring.kuapi.staff.deptinfo;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public class StaffScrapInfo extends ScrapInfo {

    private final List<String> pfForumId;

    public StaffScrapInfo(String deptName, String[] pfForumIds) {
        this.pfForumId = Arrays.asList(pfForumIds);

        if(pfForumId.size() > 0) {
            this.baseUrl = "http://home.konkuk.ac.kr/cms/Common/Professor/ProfessorList.do";
        } else {
            if(deptName.equals("리빙디자인학과")) {
                this.baseUrl = "http://www.konkuk.ac.kr/jsp/Coll/coll_01_13_01_05_tab01.jsp";
            } else if(deptName.equals("부동산학과")) {
                this.baseUrl = "http://www.realestate.ac.kr/gb/bbs/board.php?bo_table=faculty";
            } else {
                this.baseUrl = "http://www.konkuk.ac.kr/jsp/Coll/coll_01_13_01_01_tab01.jsp";
            }
        }
    }
}
