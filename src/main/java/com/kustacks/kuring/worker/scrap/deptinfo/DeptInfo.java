package com.kustacks.kuring.worker.scrap.deptinfo;

import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.worker.scrap.client.notice.LatestPageProperties;
import com.kustacks.kuring.worker.scrap.client.notice.NoticeApiClient;
import com.kustacks.kuring.worker.scrap.dto.ScrapingResultDto;
import com.kustacks.kuring.worker.scrap.parser.notice.NoticeHtmlParser;
import com.kustacks.kuring.worker.scrap.parser.notice.RowsDto;
import lombok.Getter;
import org.jsoup.nodes.Document;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Getter
public class DeptInfo {

    protected LatestPageProperties latestPageProperties;
    protected String code;
    protected DepartmentName departmentName;
    protected String collegeName;
    protected StaffScrapInfo staffScrapInfo;
    protected NoticeScrapInfo noticeScrapInfo;
    protected NoticeApiClient<ScrapingResultDto, DeptInfo> noticeApiClient;
    protected NoticeHtmlParser htmlParser;

    public List<ScrapingResultDto> scrapLatestPageHtml() {
        return noticeApiClient.request(this);
    }

    public List<ScrapingResultDto> scrapAllPageHtml() {
        return noticeApiClient.requestAll(this);
    }

    public RowsDto parse(Document document) {
        return htmlParser.parse(document);
    }

    public String getDeptName() {
        return departmentName.getKorName();
    }

    public String createRequestUrl(int index, int curPage, int pageNum) {
        return UriComponentsBuilder.fromUriString(latestPageProperties.getListUrl())
                .queryParam("siteId", noticeScrapInfo.getSiteId())
                .queryParam("boardSeq", noticeScrapInfo.getBoardSeqs().get(index))
                .queryParam("menuSeq", noticeScrapInfo.getMenuSeqs().get(index))
                .queryParam("curPage", curPage)
                .queryParam("pageNum", pageNum)
                .buildAndExpand(departmentName.getHostPrefix())
                .toUriString();
    }

    public String createViewUrl(int index) {
        return UriComponentsBuilder
                .fromUriString(latestPageProperties.getViewUrl())
                .queryParam("siteId", noticeScrapInfo.getSiteId())
                .queryParam("boardSeq", noticeScrapInfo.getBoardSeqs().get(index))
                .queryParam("menuSeq", noticeScrapInfo.getMenuSeqs().get(index))
                .queryParam("seq", "")
                .buildAndExpand(departmentName.getHostPrefix())
                .toUriString();
    }

    @Override
    public String toString() {
        return departmentName.getName();
    }
}

