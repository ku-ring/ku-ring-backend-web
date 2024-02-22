package com.kustacks.kuring.worker.scrap.parser.staff;

import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.art_design.CommunicationDesignDept;
import com.kustacks.kuring.worker.scrap.deptinfo.art_design.LivingDesignDept;
import lombok.NoArgsConstructor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class LivingAndCommunicationDesignStaffHtmlParser extends StaffHtmlParserTemplate {

    @Override
    public boolean support(DeptInfo deptInfo) {
        return deptInfo instanceof LivingDesignDept || deptInfo instanceof CommunicationDesignDept;
    }

    protected Elements selectStaffInfoRows(Document document) {
        Elements table = document.select("div.contents_area table");
        return table.select("tbody tr");
    }

    protected String[] extractStaffInfoFromRow(Element row) {
        Elements tds = row.getElementsByTag("td");

        String name = tds.get(0).getElementsByTag("a").get(0).text().trim();
        String major = tds.get(1).text().trim();
        String lab = tds.get(2).text().trim();
        String phone = tds.get(3).text().trim();
        String email = tds.get(4).text().trim();

        return new String[]{name, major, lab, phone, email};
    }
}
