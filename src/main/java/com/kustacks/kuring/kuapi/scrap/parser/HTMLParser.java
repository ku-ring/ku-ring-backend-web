package com.kustacks.kuring.kuapi.scrap.parser;

import com.kustacks.kuring.error.InternalLogicException;
import com.kustacks.kuring.kuapi.staff.deptinfo.DeptInfo;
import org.jsoup.nodes.Document;

import java.util.List;

public interface HTMLParser {

    boolean support(DeptInfo deptInfo);

    List<String[]> parse(Document document) throws InternalLogicException;
}
