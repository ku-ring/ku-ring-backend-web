package com.kustacks.kuring.worker.scrap.deptinfo;

import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.worker.dto.ScrapingResultDto;
import com.kustacks.kuring.worker.parser.notice.NoticeHtmlParserTemplate;
import com.kustacks.kuring.worker.parser.notice.RowsDto;
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
    protected boolean graduate;
    protected NoticeScrapInfo noticeGraduationInfo;


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
        NoticeScrapInfo targetNoticeScrapInfo = graduate ? noticeGraduationInfo : noticeScrapInfo;

        return UriComponentsBuilder
                .fromUriString(latestPageNoticeProperties.listUrl())
                .queryParam("page", page)
                .queryParam("row", row)
                .buildAndExpand(
                        targetNoticeScrapInfo.getSiteName(),
                        targetNoticeScrapInfo.getSiteName(),
                        targetNoticeScrapInfo.getSiteId()
                ).toUriString();
    }


    public String createViewUrl() {
        NoticeScrapInfo targetNoticeScrapInfo = graduate ? noticeGraduationInfo : noticeScrapInfo;

        return latestPageNoticeProperties.viewUrl()
                .replaceAll("\\{department\\}", targetNoticeScrapInfo.getSiteName())
                .replace("{siteId}", String.valueOf(targetNoticeScrapInfo.getSiteId()));
    }

    public boolean isSupportStaffScrap() {
        return !this.staffScrapInfo.getSiteIds().isEmpty();
    }

    @Override
    public String toString() {
        return departmentName.getName();
    }

    public void setgraduate(boolean graduate) {
        this.graduate = graduate;
    }
}

