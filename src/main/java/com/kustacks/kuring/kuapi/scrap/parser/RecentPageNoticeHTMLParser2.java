package com.kustacks.kuring.kuapi.scrap.parser;

import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.error.InternalLogicException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class RecentPageNoticeHTMLParser2 implements NoticeHTMLParser {

    @Override
    public List<String[]> parse(Document document) throws InternalLogicException {
        List<String[]> result = new LinkedList<>();

        try {
            Elements rows = document.select(".list_item");
            rows.remove(0); // 첫 번쩨 요소인 .list_item .head 삭제
            for (Element row : rows) {
                String[] oneNoticeInfo = new String[3];
                Element subjectAndIdElement = row.selectFirst(".subject a");
                Element postedDateElement = row.selectFirst(".subject_info > .list_date > span");

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
