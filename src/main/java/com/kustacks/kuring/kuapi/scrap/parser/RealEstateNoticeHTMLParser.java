package com.kustacks.kuring.kuapi.scrap.parser;

import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.error.InternalLogicException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.LinkedList;
import java.util.List;

@Component
public class RealEstateNoticeHTMLParser implements NoticeHTMLParser {

    @Override
    public List<String[]> parse(Document document) throws InternalLogicException {
        List<String[]> result = new LinkedList<>();
        Elements rows = document.select(".board_list > table > tbody > tr");
        try {
            for (Element row : rows) {
                String[] oneNoticeInfo = new String[3];
                Elements tds = row.getElementsByTag("td");

                Element subjectAndIdElement = tds.get(2).getElementsByTag("a").get(0);

                UriComponents hrefUri = UriComponentsBuilder.fromUriString(subjectAndIdElement.attr("href")).build();
                MultiValueMap<String, String> queryParams = hrefUri.getQueryParams();

                oneNoticeInfo[0] = queryParams.get("wr_id").get(0);
                oneNoticeInfo[1] = tds.get(3).ownText();
                oneNoticeInfo[2] = subjectAndIdElement.ownText();

                result.add(oneNoticeInfo);
            }
        } catch(NullPointerException | IndexOutOfBoundsException e) {
            throw new InternalLogicException(ErrorCode.NOTICE_SCRAPER_CANNOT_PARSE, e);
        }

        return result;
    }
}
