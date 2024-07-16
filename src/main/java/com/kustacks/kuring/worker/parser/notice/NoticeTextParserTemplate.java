package com.kustacks.kuring.worker.parser.notice;

import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.notice.domain.CategoryName;
import org.jsoup.nodes.Document;

public abstract class NoticeTextParserTemplate {

    public PageTextDto parse(Document document) {
        try {
            String title = extractTitle(document);
            String articleId = extractArticleId(document);
            String textBody = extractTextBody(document);

            return new PageTextDto(title, articleId, textBody);
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            throw new InternalLogicException(ErrorCode.NOTICE_SCRAPER_CANNOT_PARSE, e);
        }
    }

    protected abstract boolean support(CategoryName categoryName);

    protected abstract String extractTitle(Document document);

    protected abstract String extractArticleId(Document document);

    protected abstract String extractTextBody(Document document);
}
