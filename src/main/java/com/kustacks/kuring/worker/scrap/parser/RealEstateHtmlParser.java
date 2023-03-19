package com.kustacks.kuring.worker.scrap.parser;

import com.kustacks.kuring.common.error.ErrorCode;
import com.kustacks.kuring.common.error.InternalLogicException;
import com.kustacks.kuring.worker.update.staff.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.update.staff.deptinfo.real_estate.RealEstateDept;
import lombok.NoArgsConstructor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor
@Component
public class RealEstateHtmlParser implements HtmlParser {

    @Override
    public boolean support(DeptInfo deptInfo) {
        return deptInfo instanceof RealEstateDept;
    }

    @Override
    public List<String[]> parse(Document document) throws InternalLogicException {

        List<String[]> result = new LinkedList<>();

        try {
            Element table = document.select(".sub0201_list").get(0).getElementsByTag("ul").get(0);
            Elements rows = table.getElementsByTag("li");

            for (Element row : rows) {
                String[] oneStaffInfo = new String[5];
                Element content = row.select(".con").get(0);

                oneStaffInfo[0] = content.select("dl > dt > a > strong").get(0).text();
                oneStaffInfo[1] = String.valueOf(content.select("dl > dd").get(0).childNode(4)).replaceFirst("\\s", "").trim();

                Element textMore = content.select(".text_more").get(0);

                oneStaffInfo[2] = String.valueOf(textMore.childNode(4)).split(":")[1].replaceFirst("\\s", "").trim();
                oneStaffInfo[3] = String.valueOf(textMore.childNode(6)).split(":")[1].replaceFirst("\\s", "").trim();
                oneStaffInfo[4] = textMore.getElementsByTag("a").get(0).text();

                result.add(oneStaffInfo);
            }
        } catch(NullPointerException | IndexOutOfBoundsException e) {
            throw new InternalLogicException(ErrorCode.STAFF_SCRAPER_CANNOT_PARSE, e);
        }

        return result;
    }
}
