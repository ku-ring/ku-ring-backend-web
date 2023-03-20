package com.kustacks.kuring.worker.scrap.parser;

import com.kustacks.kuring.common.error.InternalLogicException;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import org.jsoup.nodes.Document;

import java.util.List;

public interface HtmlParser {

    boolean support(DeptInfo deptInfo);

    List<String[]> parse(Document document) throws InternalLogicException;
}
