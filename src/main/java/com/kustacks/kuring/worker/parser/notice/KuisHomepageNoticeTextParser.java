package com.kustacks.kuring.worker.parser.notice;

import com.kustacks.kuring.notice.domain.CategoryName;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Component
public class KuisHomepageNoticeTextParser extends NoticeTextParserTemplate {

    @Override
    protected boolean support(CategoryName categoryName) {
        return categoryName != CategoryName.LIBRARY;
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
        Element boardContent = document.selectFirst("div.board_content");
        if(boardContent != null) {
            return boardContent.text();
        }

        boardContent = document.selectFirst("div.view-con");
        if(boardContent != null) {
            return boardContent.text();
        }

        throw new IllegalArgumentException("공지의 본문을 찾을 수 없습니다.");
    }
}
