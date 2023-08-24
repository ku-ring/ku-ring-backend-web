package com.kustacks.kuring.worker.scrap.parser.notice;

import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.scrap.parser.HtmlParser;
import org.jsoup.nodes.Document;

public interface NoticeHtmlParser extends HtmlParser {

    boolean support(DeptInfo deptInfo);

    RowsDto parse(Document document) throws InternalLogicException;
}
