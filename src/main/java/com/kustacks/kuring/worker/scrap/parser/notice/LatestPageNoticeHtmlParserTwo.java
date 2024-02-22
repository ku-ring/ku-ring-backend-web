package com.kustacks.kuring.worker.scrap.parser.notice;

import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.real_estate.RealEstateDept;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Component
public class LatestPageNoticeHtmlParserTwo extends NoticeHtmlParserTemplate {

    @Override
    public boolean support(DeptInfo deptInfo) {
        return !(deptInfo instanceof RealEstateDept);
    }

    @Override
    protected Elements selectImportantRows(Document document) {
        return document.select(".notice");
    }

    @Override
    protected Elements selectNormalRows(Document document) {
        return document.select(".list_item").not(".notice").not(".thead");
    }

    @Override
    protected String[] extractNoticeFromRow(Element row) {
        String[] oneNoticeInfo = new String[3];
        Element subjectAndIdElement = row.selectFirst(".subject a");
        Element postedDateElement = row.selectFirst(".subject_info > .list_date > span");

        oneNoticeInfo[0] = subjectAndIdElement.attr("data-itsp-view-link"); // articleId
        oneNoticeInfo[1] = postedDateElement.ownText(); // postedDate
        oneNoticeInfo[2] = subjectAndIdElement.ownText(); // subject
        return oneNoticeInfo;
    }
}
