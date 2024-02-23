package com.kustacks.kuring.worker.scrap.parser.notice;

import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.real_estate.RealEstateDept;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Component
public class LatestPageNoticeHtmlParser extends NoticeHtmlParserTemplate {

    @Override
    public boolean support(DeptInfo deptInfo) {
        return !(deptInfo instanceof RealEstateDept);
    }

    @Override
    protected Elements selectImportantRows(Document document) {
        return document.select("#noticeList > tr");
    }

    @Override
    protected Elements selectNormalRows(Document document) {
        return document.select("#dispList > tr");
    }

    @Override
    protected String[] extractNoticeFromRow(Element row) {
        String[] oneNoticeInfo = new String[3];
        Elements tds = row.getElementsByTag("td");

        Element subjectAndIdElement = tds.get(1).getElementsByTag("a").get(0);
        Element postedDateElement = tds.get(3);

        oneNoticeInfo[0] = subjectAndIdElement.attr("data-itsp-view-link"); // articleId
        oneNoticeInfo[1] = postedDateElement.ownText(); // postedDate
        oneNoticeInfo[2] = subjectAndIdElement.ownText(); // subject
        return oneNoticeInfo;
    }
}
