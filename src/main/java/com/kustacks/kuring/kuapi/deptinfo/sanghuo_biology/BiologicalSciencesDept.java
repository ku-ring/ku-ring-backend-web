package com.kustacks.kuring.kuapi.deptinfo.sanghuo_biology;

import com.kustacks.kuring.kuapi.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.kuapi.deptinfo.StaffScrapInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

// 생명과학특성학과의 baseUrl = http://www.konkuk.ac.kr/cms/Common/MessageBoard/ArticleList.do
// 생명과학특성학과는 게시글 링크를 비공개로 해놔서 파싱할 수가 없음. 그래서 forumIds를 주석처리해둠.
@Component
public class BiologicalSciencesDept extends SanghuoBiologyCollege {

    public BiologicalSciencesDept() {
        super();

        List<String> pfForumIds = new ArrayList<>(1);
        pfForumIds.add("6182");

        List<String> forumIds = new ArrayList<>(1);
//        forumIds.add("11676214");

        List<String> boardSeqs = new ArrayList<>(0);

        List<String> menuSeqs = new ArrayList<>(0);

        this.staffScrapInfo = new StaffScrapInfo(pfForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "BIOSCIENCE", boardSeqs, menuSeqs);
        this.code = "126906";
        this.deptName = "생명과학특성학과";
    }
}
