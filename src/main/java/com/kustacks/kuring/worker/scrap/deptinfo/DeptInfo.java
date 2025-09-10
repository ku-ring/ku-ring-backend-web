package com.kustacks.kuring.worker.scrap.deptinfo;

import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.worker.dto.ScrapingResultDto;
import com.kustacks.kuring.worker.parser.notice.NoticeHtmlParserTemplate;
import com.kustacks.kuring.worker.parser.notice.RowsDto;
import com.kustacks.kuring.worker.scrap.client.notice.LatestPageGraduateNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.NoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.LatestPageNoticeProperties;
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
    protected NoticeScrapInfo noticeGraduationInfo;
    protected LatestPageGraduateNoticeApiClient latestPageGraduateNoticeApiClient;


    public List<ScrapingResultDto> scrapLatestPageHtml() {
        return noticeApiClient.request(this);
    }

    public List<ScrapingResultDto> scrapAllPageHtml() {
        return noticeApiClient.requestAll(this);
    }

    public List<ScrapingResultDto> scrapGraduateLatestPageHtml() {
        return latestPageGraduateNoticeApiClient.request(this);
    }

    public List<ScrapingResultDto> scrapGraduateAllPageHtml() {
        return latestPageGraduateNoticeApiClient.requestAll(this);
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

    public String createUndergraduateRequestUrl(int page, int row) {
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

    public String createUndergraduateViewUrl() {
        return latestPageNoticeProperties.viewUrl()
                .replaceAll("\\{department\\}", noticeScrapInfo.getSiteName())
                .replace("{siteId}", String.valueOf(noticeScrapInfo.getSiteId()));
    }

    public String createGraduateRequestUrl(int page, int row) {
        return UriComponentsBuilder
                .fromUriString(latestPageNoticeProperties.listUrl())
                .queryParam("page", page)
                .queryParam("row", row)
                .buildAndExpand(
                        noticeGraduationInfo.getSiteName(),
                        noticeGraduationInfo.getSiteName(),
                        noticeGraduationInfo.getSiteId()
                ).toUriString();
    }

    public String createGraduateViewUrl() {
        return latestPageNoticeProperties.viewUrl()
                .replaceAll("\\{department\\}", noticeGraduationInfo.getSiteName())
                .replace("{siteId}", String.valueOf(noticeGraduationInfo.getSiteId()));
    }

    public boolean isSupportStaffScrap() {
        return !this.staffScrapInfo.getSiteIds().isEmpty();
    }

    public boolean isSupportGraduateScrap() {
        return this.noticeGraduationInfo != null;
    }

    @Override
    public String toString() {
        return departmentName.getName();
    }

}

