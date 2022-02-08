package com.kustacks.kuring.kuapi.scrap.parser;

import com.kustacks.kuring.error.InternalLogicException;
import com.kustacks.kuring.kuapi.deptinfo.DeptInfo;
import org.jsoup.nodes.Document;

import java.util.List;

public interface HTMLParser {

    List<String[]> parse(Document document) throws InternalLogicException;
}
