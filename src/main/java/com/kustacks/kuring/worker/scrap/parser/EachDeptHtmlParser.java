package com.kustacks.kuring.worker.scrap.parser;

import com.kustacks.kuring.common.error.ErrorCode;
import com.kustacks.kuring.common.error.InternalLogicException;
import com.kustacks.kuring.worker.update.staff.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.update.staff.deptinfo.art_design.CommunicationDesignDept;
import com.kustacks.kuring.worker.update.staff.deptinfo.art_design.LivingDesignDept;
import com.kustacks.kuring.worker.update.staff.deptinfo.real_estate.RealEstateDept;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@NoArgsConstructor
@Component
public class EachDeptHtmlParser implements HtmlParser {

    @Override
    public boolean support(DeptInfo deptInfo) {
        return !(deptInfo instanceof RealEstateDept) &&
                !(deptInfo instanceof LivingDesignDept) &&
                !(deptInfo instanceof CommunicationDesignDept);
    }

    @Override
    public List<String[]> parse(Document document) throws InternalLogicException {

        List<String[]> result = new LinkedList<>();

        try {
            Element table = document.select(".photo_intro").get(0);
            Elements rows = table.getElementsByTag("dl");

            for (Element row : rows) {
                String[] oneStaffInfo = new String[5];
                Elements infos = row.getElementsByTag("dd");

                // 교수명, 직위, 세부전공, 연구실, 연락처, 이메일 순으로 파싱
                // 연구실, 연락처 정보는 없는 경우가 종종 있으므로, childNode접근 전 인덱스 체크하는 로직을 넣었음
                oneStaffInfo[0] = infos.get(0).getElementsByTag("span").get(1).text();

                String jobPosition = String.valueOf(infos.get(1).childNodeSize() < 2 ? "" : infos.get(1).childNode(1));
                if(jobPosition.contains("명예") || jobPosition.contains("대우") || jobPosition.contains("휴직") || !jobPosition.contains("교수")) {
                    log.info("스크래핑 스킵 -> {} 교수", oneStaffInfo[0]);
                    continue;
                }
//                if() {
//                    throw new InternalLogicException(ErrorCode.STAFF_SCRAPER_CANNOT_PARSE);
//                }

                oneStaffInfo[1] = infos.get(2).childNodeSize() < 2 ? "" : String.valueOf(infos.get(2).childNode(1));
                oneStaffInfo[2] = infos.get(3).childNodeSize() < 2 ? "" : String.valueOf(infos.get(3).childNode(1));
                oneStaffInfo[3] = infos.get(4).childNodeSize() < 2 ? "" : String.valueOf(infos.get(4).childNode(1));
                oneStaffInfo[4] = infos.get(5).getElementsByTag("a").get(0).text();

                result.add(oneStaffInfo);
            }
        } catch(NullPointerException | IndexOutOfBoundsException e) {
            throw new InternalLogicException(ErrorCode.STAFF_SCRAPER_CANNOT_PARSE, e);
        }

        return result;
    }
}
