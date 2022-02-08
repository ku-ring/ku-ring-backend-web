package com.kustacks.kuring.kuapi.deptinfo.art_design;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MovingImageFilmDept extends ArtDesignCollege {

    public MovingImageFilmDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("15032480");

        List<String> forumIds = new ArrayList<>(0);

        List<String> boardSeqs = new ArrayList<>(1);
        boardSeqs.add("580");

        List<String> menuSeqs = new ArrayList<>(1);
        menuSeqs.add("4508");

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "MOVINGIMAGES", boardSeqs, menuSeqs);
        this.code = "127128";
        this.deptName = "영상영화학과";
    }
}
