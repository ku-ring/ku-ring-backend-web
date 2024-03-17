package com.kustacks.kuring.worker.scrap.deptinfo.sanghuo_biology;

import com.kustacks.kuring.worker.parser.notice.LatestPageNoticeHtmlParser;
import com.kustacks.kuring.worker.scrap.client.notice.LatestPageNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.LatestPageNoticeProperties;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.List;

import static com.kustacks.kuring.notice.domain.DepartmentName.BIO_SCIENCE;

// 생명과학특성학과의 baseUrl = http://www.konkuk.ac.kr/cms/Common/MessageBoard/ArticleList.do
// 생명과학특성학과는 게시글 링크를 비공개로 해놔서 파싱할 수가 없음. 그래서 forumIds를 주석처리해둠.
@RegisterDepartmentMap(key = BIO_SCIENCE)
public class BiologicalSciencesDept extends SanghuoBiologyCollege {

    public BiologicalSciencesDept(
            LatestPageNoticeApiClient latestPageNoticeApiClient,
            LatestPageNoticeHtmlParser latestPageNoticeHtmlParser,
            LatestPageNoticeProperties latestPageNoticeProperties
    ) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;
        this.latestPageNoticeProperties = latestPageNoticeProperties;

        List<String> professorForumIds = List.of("10864");
        this.staffScrapInfo = new StaffScrapInfo(professorForumIds);
        this.noticeScrapInfo = new NoticeScrapInfo(BIO_SCIENCE.getHostPrefix(), 909);
        this.departmentName = BIO_SCIENCE;
    }
}
