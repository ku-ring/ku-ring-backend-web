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
        Element table = document.select(".sub0201_list").get(0).getElementsByTag("ul").get(0);
        return table.getElementsByTag("li");
    }

    protected String[] extractStaffInfoFromRow(Element row) {
        Element content = row.select(".con").get(0);

        String name = content.select("dl > dt > a > strong").get(0).text();
        String major = String.valueOf(content.select("dl > dd").get(0).childNode(4)).replaceFirst("\\s", "").trim();

        Element textMore = content.select(".text_more").get(0);

        String lab = String.valueOf(textMore.childNode(4)).split(":")[1].replaceFirst("\\s", "").trim();
        String phone = String.valueOf(textMore.childNode(6)).split(":")[1].replaceFirst("\\s", "").trim();
        String email = textMore.getElementsByTag("a").get(0).text();
        return new String[]{name, major, lab, phone, email};
    }
}
