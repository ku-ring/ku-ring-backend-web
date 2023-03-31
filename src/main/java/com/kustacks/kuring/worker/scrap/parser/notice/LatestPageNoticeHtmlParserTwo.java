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
public class LatestPageNoticeHtmlParserTwo implements NoticeHtmlParser {

    @Override
    public boolean support(DeptInfo deptInfo) {
        return true;
    }

    @Override
    public RowsDto parse(Document document) {
        try {
            Elements importantRows = document.select(".notice");
            Elements normalRows = document.select(".list_item").not(".notice").not(".thead");

            List<String[]> importantRowList = extractNoticeListFromRows(importantRows);
            List<String[]> normalRowList = extractNoticeListFromRows(normalRows);

            return new RowsDto(importantRowList, normalRowList);
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            throw new InternalLogicException(ErrorCode.NOTICE_SCRAPER_CANNOT_PARSE, e);
        }
    }

    private List<String[]> extractNoticeListFromRows(Elements rows) {
        return rows.stream()
                .map(LatestPageNoticeHtmlParserTwo::extractNoticeFromRow)
                .collect(Collectors.toList());
    }

    private static String[] extractNoticeFromRow(Element row) {
        String[] oneNoticeInfo = new String[3];
        Element subjectAndIdElement = row.selectFirst(".subject a");
        Element postedDateElement = row.selectFirst(".subject_info > .list_date > span");

        oneNoticeInfo[0] = subjectAndIdElement.attr("data-itsp-view-link"); // articleId
        oneNoticeInfo[1] = postedDateElement.ownText(); // postedDate
        oneNoticeInfo[2] = subjectAndIdElement.ownText(); // subject
        return oneNoticeInfo;
    }
}
