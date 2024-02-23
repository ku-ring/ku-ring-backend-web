package com.kustacks.kuring.worker.scrap.parser.staff;

import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

public abstract class StaffHtmlParserTemplate {

    public List<String[]> parse(Document document) throws InternalLogicException {
        try {
            Elements rows = selectStaffInfoRows(document);

            return rows.stream()
                    .map(this::extractStaffInfoFromRow)
                    .toList();
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            throw new InternalLogicException(ErrorCode.STAFF_SCRAPER_CANNOT_PARSE, e);
        }
    }

    public abstract boolean support(DeptInfo deptInfo);
    protected abstract Elements selectStaffInfoRows(Document document);
    protected abstract String[] extractStaffInfoFromRow(Element row);
}
