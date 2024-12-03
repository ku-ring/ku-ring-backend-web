package com.kustacks.kuring.worker.parser.staff;

import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.real_estate.RealEstateDept;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Component
public class RealEstateStaffHtmlParser extends StaffHtmlParserTemplate {

    @Override
    public boolean support(DeptInfo deptInfo) {
        return deptInfo instanceof RealEstateDept;
    }
    protected Elements selectStaffInfoRows(Document document) {
        return document.select(".row");
    }

    protected String[] extractStaffInfoFromRow(Element row) {
        String name = row.select(".info .title .name").text();

        Elements detalTagElement = row.select(".detail");
        String jobPosition = detalTagElement.select("dt:contains(직위) + dd").text();
        String major = detalTagElement.select("dt:contains(연구분야) + dd").text().trim();
        String lab = detalTagElement.select("dt:contains(연구실) + dd").text().trim();
        String extensionNumber = detalTagElement.select("dt:contains(연락처) + dd").text().trim();
        String email = detalTagElement.select("dt:contains(이메일) + dd").text().trim();
        return new String[]{name, jobPosition, major, lab, extensionNumber, email};
    }
}

