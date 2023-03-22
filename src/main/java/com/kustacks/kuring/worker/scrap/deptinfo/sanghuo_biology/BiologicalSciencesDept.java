package com.kustacks.kuring.worker.scrap.deptinfo.sanghuo_biology;

import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.Collections;
import java.util.List;

// 생명과학특성학과의 baseUrl = http://www.konkuk.ac.kr/cms/Common/MessageBoard/ArticleList.do
// 생명과학특성학과는 게시글 링크를 비공개로 해놔서 파싱할 수가 없음. 그래서 forumIds를 주석처리해둠.
@RegisterDepartmentMap(key = DepartmentName.BIO_SCIENCE)
public class BiologicalSciencesDept extends SanghuoBiologyCollege {

    public BiologicalSciencesDept() {
        super();
        List<String> professorForumIds = List.of("6182");
        List<String> forumIds = Collections.emptyList();
//        forumIds.add("11676214");
        List<String> boardSeqs = Collections.emptyList();
        List<String> menuSeqs = Collections.emptyList();

        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(forumIds, "BIOSCIENCE", boardSeqs, menuSeqs);
        this.code = "126906";
        this.deptName = DepartmentName.BIO_SCIENCE.getKorName();
    }
}
