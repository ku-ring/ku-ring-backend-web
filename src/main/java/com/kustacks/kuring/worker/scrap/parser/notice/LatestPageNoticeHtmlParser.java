package com.kustacks.kuring.worker.scrap.parser.notice;

import com.kustacks.kuring.common.error.ErrorCode;
import com.kustacks.kuring.common.error.InternalLogicException;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LatestPageNoticeHtmlParser implements NoticeHtmlParser {

    @Override
    public boolean support(DeptInfo deptInfo) {
        return true;
    }

    @Override
    public List<String[]> parse(Document document) {
        try {
            Elements rows = document.select("#noticeList > tr");
            rows.addAll(document.select("#dispList > tr"));
            return extractNoticeListFromRows(rows);
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            throw new InternalLogicException(ErrorCode.NOTICE_SCRAPER_CANNOT_PARSE, e);
        }
    }

    private List<String[]> extractNoticeListFromRows(Elements rows) {
        return rows.stream()
                .map(LatestPageNoticeHtmlParser::extractNoticeFromRow)
                .collect(Collectors.toList());
    }

    private static String[] extractNoticeFromRow(Element row) {
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
