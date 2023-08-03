package com.kustacks.kuring.worker.scrap.parser.staff;

import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.scrap.parser.HtmlParser;
import org.jsoup.nodes.Document;

import java.util.List;

public interface StaffHtmlParser extends HtmlParser {

    boolean support(DeptInfo deptInfo);

    List<String[]> parse(Document document) throws InternalLogicException;
}
