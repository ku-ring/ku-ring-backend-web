package com.kustacks.kuring.worker.scrap.parser.notice;

import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.real_estate.RealEstateDept;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Component
public class RealEstateNoticeHtmlParser implements NoticeHtmlParser {

    @Override
    public boolean support(DeptInfo deptInfo) {
        return deptInfo instanceof RealEstateDept;
    }

    @Override
    public RowsDto parse(Document document) {
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
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            throw new InternalLogicException(ErrorCode.NOTICE_SCRAPER_CANNOT_PARSE, e);
        }

        return new RowsDto(Collections.emptyList(), result);
    }
}
