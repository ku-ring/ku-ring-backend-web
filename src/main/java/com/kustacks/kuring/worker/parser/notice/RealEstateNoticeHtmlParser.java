package com.kustacks.kuring.worker.parser.notice;

import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.real_estate.RealEstateDept;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class RealEstateNoticeHtmlParser extends NoticeHtmlParserTemplate {

    @Override
    public boolean support(DeptInfo deptInfo) {
        return deptInfo instanceof RealEstateDept;
    }

    @Override
    protected Elements selectImportantRows(Document document) {
        return new Elements();
    }

    @Override
    protected Elements selectNormalRows(Document document) {
        return document.select(".board_list > table > tbody > tr");
    }

    @Override
    protected String[] extractNoticeFromRow(Element row) {
        String[] oneNoticeInfo = new String[3];
        Elements tds = row.getElementsByTag("td");

        Element subjectAndIdElement = tds.get(2).getElementsByTag("a").get(0);

        UriComponents hrefUri = UriComponentsBuilder.fromUriString(subjectAndIdElement.attr("href")).build();
        MultiValueMap<String, String> queryParams = hrefUri.getQueryParams();

        oneNoticeInfo[0] = queryParams.get("wr_id").get(0);
        oneNoticeInfo[1] = tds.get(3).ownText();
        oneNoticeInfo[2] = subjectAndIdElement.ownText();
        return oneNoticeInfo;
    }
}
