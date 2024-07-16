package com.kustacks.kuring.worker.parser.notice;

import com.kustacks.kuring.notice.domain.CategoryName;
import org.jsoup.nodes.Document;

public class KuisHomepageNoticeTextParser extends NoticeTextParserTemplate {

    @Override
    protected boolean support(CategoryName categoryName) {
        return !(categoryName == CategoryName.LIBRARY);
    }

    @Override
    protected String extractTitle(Document document) {
        return document.selectFirst("h2.view-title").text().trim();
    }

    @Override
    protected String extractArticleId(Document document) {
        return document.selectFirst("dl.view-num dd").text().trim();
    }

    @Override
    protected String extractTextBody(Document document) {
        return document.selectFirst("div.board_content").text();
    }
}
