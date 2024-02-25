package com.kustacks.kuring.worker.scrap.deptinfo;

import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.worker.scrap.client.notice.property.LatestPageNoticeProperties;
import com.kustacks.kuring.worker.scrap.client.notice.NoticeApiClient;
import com.kustacks.kuring.worker.dto.ScrapingResultDto;
import com.kustacks.kuring.worker.scrap.parser.notice.NoticeHtmlParserTemplate;
import com.kustacks.kuring.worker.scrap.parser.notice.RowsDto;
import lombok.Getter;
import org.jsoup.nodes.Document;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Getter
public class DeptInfo {

    protected NoticeApiClient<ScrapingResultDto, DeptInfo> noticeApiClient;
    protected LatestPageNoticeProperties latestPageNoticeProperties;
    protected NoticeHtmlParserTemplate htmlParser;
    protected NoticeScrapInfo noticeScrapInfo;
    protected StaffScrapInfo staffScrapInfo;
    protected DepartmentName departmentName;
    protected String collegeName;

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

    public boolean isSameDepartment(DepartmentName departmentName) {
        return this.departmentName.equals(departmentName);
    }

    public List<String> getProfessorForumIds() {
        return this.staffScrapInfo.getProfessorForumId();
    }

    // TODO : page=1&row=200 처럼 사용해야 함
    public String createRequestUrl(int page, int row) {
        return UriComponentsBuilder
                .fromUriString(latestPageNoticeProperties.getListUrl())
                .queryParam("page", page)
                .queryParam("row", row)
                .buildAndExpand(
                        departmentName.getHostPrefix(),
                        departmentName.getHostPrefix(),
                        noticeScrapInfo.getSiteId()
                ).toUriString();
    }

    public String createViewUrl() {
        return UriComponentsBuilder
                .fromUriString(latestPageNoticeProperties.getViewUrl())
                .buildAndExpand(
                        noticeScrapInfo.getSiteName(),
                        noticeScrapInfo.getSiteName(),
                        noticeScrapInfo.getSiteId()
                ).toUriString();
    }

    @Override
    public String toString() {
        return departmentName.getName();
    }
}

