package com.kustacks.kuring.worker.scrap.parser;

import com.kustacks.kuring.common.error.InternalLogicException;
import com.kustacks.kuring.worker.update.staff.deptinfo.DeptInfo;
import org.jsoup.nodes.Document;

import java.util.List;

public interface HTMLParser {

    boolean support(DeptInfo deptInfo);

    List<String[]> parse(Document document) throws InternalLogicException;
}
