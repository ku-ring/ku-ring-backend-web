package com.kustacks.kuring.worker.scrap.parser.notice;

import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import org.jsoup.nodes.Document;

public interface NoticeHtmlParser {

    boolean support(DeptInfo deptInfo);

    RowsDto parse(Document document) throws InternalLogicException;
}
