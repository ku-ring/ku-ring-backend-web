package com.kustacks.kuring.worker.parser.staff;

import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.real_estate.RealEstateDept;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Slf4j
@NoArgsConstructor
@Component
public class EachDeptStaffHtmlParser extends StaffHtmlParserTemplate {

    @Override
    public boolean support(DeptInfo deptInfo) {
        return !(deptInfo instanceof RealEstateDept);
    }

    protected Elements selectStaffInfoRows(Document document) {
        return document.select(".row");
    }

    protected String[] extractStaffInfoFromRow(Element row) {
        String name = row.select(".info .title .name").text();

        Elements detailElement = row.select(".detail");
        String jobPosition = detailElement.select(".ico1 dd").text().trim();
        String major = detailElement.select(".ico2 dd").text().trim();
        String lab = detailElement.select(".ico3 dd").text().trim();
        String extensionNumber = detailElement.select(".ico4 dd").text().trim();
        String email = detailElement.select(".ico5 dd").text().trim();
        return new String[]{name, jobPosition, major, lab, extensionNumber, email};
    }
}
