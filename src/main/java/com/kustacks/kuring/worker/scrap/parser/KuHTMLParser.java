package com.kustacks.kuring.worker.scrap.parser;

import com.kustacks.kuring.common.error.ErrorCode;
import com.kustacks.kuring.common.error.InternalLogicException;
import com.kustacks.kuring.worker.update.staff.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.update.staff.deptinfo.art_design.CommunicationDesignDept;
import com.kustacks.kuring.worker.update.staff.deptinfo.art_design.LivingDesignDept;
import lombok.NoArgsConstructor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor
@Component
public class KuHTMLParser implements HTMLParser {

    @Override
    public boolean support(DeptInfo deptInfo) {
        return deptInfo instanceof LivingDesignDept || deptInfo instanceof CommunicationDesignDept;
    }

    @Override
    public List<String[]> parse(Document document) throws InternalLogicException {

        // 테이블 추출
        Elements tables = document.getElementsByTag("table");

        // 이름, 전공, 연구실, 전화번호, 이메일 순 추출
        List<String[]> result = new LinkedList<>();

        try {
            for (Element table : tables) {

                // 물리학과의 경우 테이블이 2개임. 이를 필터링하기 위한 코드
                // colgroup 태그에서 column 개수가 5가 아니면 교수 테이블이 아니라고 판단한다.
                // 만약 colgroup이 없다면, 예상치 못한 경우이므로 에러로 처리
                Elements colgroups = table.select("colgroup");
                if(colgroups.size() == 0) {
                    throw new InternalLogicException(ErrorCode.STAFF_SCRAPER_CANNOT_PARSE);
                }

                // 컬럼 개수 6개인 학과도 있음. 컬럼 개수 6개인 학과가 스킵되지 않게 비교문 추가
                Element colgroup = colgroups.get(0);
                int colgroupSize = colgroup.childrenSize();
                if(colgroupSize != 5 && colgroupSize != 6) {
                    continue;
                }

                Elements rows = table.select("tbody > tr");
                for (Element row : rows) {
                    int idx = 0;
                    String[] oneStaffInfo = new String[5];
                    Elements tds = row.getElementsByTag("td");

                    boolean isEmailNotEmpty = true;
                    for (Element td : tds) {

                        // K뷰티산업융합학과 비고 컬럼 무시하기 위한 코드
                        if(idx >= 5) {
                            break;
                        }

                        if(td.classNames().contains("first")) {
                            oneStaffInfo[idx] = td.getElementsByTag("a").get(0).text();
                        } else {
                            // 이메일 파싱 텍스트가 이메일 형식이 아니라면 isEmailNotEmpty 플래그를 false로 설정
                            if(idx == 4 && !td.text().matches("^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$")) {
                                isEmailNotEmpty = false;
                            }
                            oneStaffInfo[idx] = td.text();
                        }
                        ++idx;
                    }

                    if(isEmailNotEmpty) {
                        result.add(oneStaffInfo);
                    }
//                else {
//                    log.info("스크래핑 스킵 -> {} {} 교수", deptName, oneStaffInfo[0]);
//                }
                }

                break;
            }
        } catch(NullPointerException | IndexOutOfBoundsException e) {
            throw new InternalLogicException(ErrorCode.STAFF_SCRAPER_CANNOT_PARSE, e);
        }

        return result;
    }
}
