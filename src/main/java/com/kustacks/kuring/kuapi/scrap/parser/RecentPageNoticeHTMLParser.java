package com.kustacks.kuring.kuapi.scrap.parser;

import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.error.InternalLogicException;
import com.kustacks.kuring.kuapi.deptinfo.DeptInfo;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class RecentPageNoticeHTMLParser implements NoticeHTMLParser {

    @Override
    public List<String[]> parse(Document document) throws InternalLogicException {
        List<String[]> result = new LinkedList<>();

        try {
            Elements rows = document.select("#noticeList > tr");
            rows.addAll(document.select("#dispList > tr"));
            for (Element row : rows) {
                String[] oneNoticeInfo = new String[3];
                Elements tds = row.getElementsByTag("td");

                Element subjectAndIdElement = tds.get(1).getElementsByTag("a").get(0);
                Element postedDateElement = tds.get(3);

                oneNoticeInfo[0] = subjectAndIdElement.attr("data-itsp-view-link"); // articleId
                oneNoticeInfo[1] = postedDateElement.ownText(); // postedDate
                oneNoticeInfo[2] = subjectAndIdElement.ownText(); // subject

                result.add(oneNoticeInfo);
            }
        } catch(NullPointerException | IndexOutOfBoundsException e) {
            throw new InternalLogicException(ErrorCode.NOTICE_SCRAPER_CANNOT_PARSE, e);
        }

        return result;
    }
}
