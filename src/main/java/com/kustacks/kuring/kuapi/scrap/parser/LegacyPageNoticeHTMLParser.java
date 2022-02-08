package com.kustacks.kuring.kuapi.scrap.parser;

import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.error.InternalLogicException;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Component
public class LegacyPageNoticeHTMLParser implements NoticeHTMLParser {

    @Override
    public List<String[]> parse(Document document) throws InternalLogicException {

        List<String[]> result = new LinkedList<>();
        Elements rows = document.select("#content .board > tbody > tr");

        try {
            for (Element row : rows) {
                String[] oneNoticeInfo = new String[3];
                Elements tds = row.getElementsByTag("td");
                // legacy 페이지에서 "게시자에 의해 삭제된 글입니다" 라고 뜨는 게시글들이 있음. 이를 필터링하기 위한 코드.
                if(tds.size() != 6) {
                    continue;
                }

                Element subjectAndIdElement = tds.get(1).getElementsByTag("a").get(0);
                Element postedDateElement = tds.get(4);

                UriComponents hrefUri = UriComponentsBuilder.fromUriString(subjectAndIdElement.attr("href")).build();
                MultiValueMap<String, String> queryParams = hrefUri.getQueryParams();

                oneNoticeInfo[0] = queryParams.get("id").get(0);
                oneNoticeInfo[1] = postedDateElement.ownText();
                oneNoticeInfo[2] = subjectAndIdElement.ownText();

                result.add(oneNoticeInfo);
            }
        } catch(NullPointerException | IndexOutOfBoundsException e) {
            System.out.println(document.html());
            log.info("", e);
            throw new InternalLogicException(ErrorCode.NOTICE_SCRAPER_CANNOT_PARSE, e);
        }

        return result;
    }
}
