package com.kustacks.kuring.worker.scrap.deptinfo.art_design;

import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class MovingImageFilmDept extends ArtDesignCollege {

    public MovingImageFilmDept() {
        super();
        List<String> professorForumIds = List.of("15032480");
        List<String> forumIds = Collections.emptyList();
        List<String> boardSeqs = List.of("580");
        List<String> menuSeqs = List.of("4508");

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "MOVINGIMAGES", boardSeqs, menuSeqs);
        this.code = "127128";
        this.deptName = "영상영화학과";
    }
}
