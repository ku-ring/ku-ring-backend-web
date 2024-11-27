package com.kustacks.kuring.worker.scrap.deptinfo;

import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.worker.scrap.client.notice.property.LatestPageNoticeProperties;
import com.kustacks.kuring.worker.scrap.client.notice.NoticeApiClient;
import com.kustacks.kuring.worker.dto.ScrapingResultDto;
import com.kustacks.kuring.worker.parser.notice.NoticeHtmlParserTemplate;
import com.kustacks.kuring.worker.parser.notice.RowsDto;
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

    public List<Integer> getStaffSiteIds() {
        return this.staffScrapInfo.getSiteIds();
    }

    public String getStaffSiteName() {
        return this.staffScrapInfo.getSiteName();
    }

    public String createRequestUrl(int page, int row) {
        return UriComponentsBuilder
                .fromUriString(latestPageNoticeProperties.listUrl())
                .queryParam("page", page)
                .queryParam("row", row)
                .buildAndExpand(
                        noticeScrapInfo.getSiteName(),
                        noticeScrapInfo.getSiteName(),
                        noticeScrapInfo.getSiteId()
                ).toUriString();
    }

    public String createViewUrl() {
        return latestPageNoticeProperties.viewUrl()
                .replaceAll("\\{department\\}", noticeScrapInfo.getSiteName())
                .replace("{siteId}", String.valueOf(noticeScrapInfo.getSiteId()));
    }

    public boolean isSupportStaffScrap() {
        return !this.staffScrapInfo.getSiteIds().isEmpty();
    }

    @Override
    public String toString() {
        return departmentName.getName();
    }
}

