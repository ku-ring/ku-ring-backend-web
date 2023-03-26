package com.kustacks.kuring.worker.scrap.deptinfo;

import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.client.notice.NoticeApiClient;
import com.kustacks.kuring.worker.scrap.dto.ScrapingResultDto;
import com.kustacks.kuring.worker.scrap.parser.HtmlParser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jsoup.nodes.Document;

import java.util.List;

@Getter
@NoArgsConstructor
public class DeptInfo {

    protected String code;
    protected DepartmentName departmentName;
    protected String collegeName;
    protected StaffScrapInfo staffScrapInfo;
    protected NoticeScrapInfo noticeScrapInfo;
    protected NoticeApiClient<ScrapingResultDto, DeptInfo> noticeApiClient;
    protected HtmlParser htmlParser;

    public List<ScrapingResultDto> scrapHtml() {
        return noticeApiClient.request(this);
    }

    public List<String[]> parse(Document document) {
        return htmlParser.parse(document);
    }

    public String getDeptName() {
        return departmentName.getKorName();
    }

    @Override
    public String toString() {
        return departmentName.getName();
    }
}
