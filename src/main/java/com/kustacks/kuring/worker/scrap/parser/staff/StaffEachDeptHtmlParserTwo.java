package com.kustacks.kuring.worker.scrap.parser.staff;

import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.art_design.CommunicationDesignDept;
import com.kustacks.kuring.worker.scrap.deptinfo.art_design.LivingDesignDept;
import lombok.NoArgsConstructor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

@Component
@NoArgsConstructor
public class StaffEachDeptHtmlParserTwo implements StaffHtmlParser {

    private static final String REGEX_PATTERN = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

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
                if (colgroups.size() == 0) {
                    throw new InternalLogicException(ErrorCode.STAFF_SCRAPER_CANNOT_PARSE);
                }

                // 컬럼 개수 6개인 학과도 있음. 컬럼 개수 6개인 학과가 스킵되지 않게 비교문 추가
                Element colgroup = colgroups.get(0);
                int colgroupSize = colgroup.childrenSize();
                if (colgroupSize != 5 && colgroupSize != 6) {
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
                        if (idx >= 5) {
                            break;
                        }

                        if (td.classNames().contains("first")) {
                            oneStaffInfo[idx] = td.getElementsByTag("a").get(0).text();
                        } else {
                            // 이메일 파싱 텍스트가 이메일 형식이 아니라면 isEmailNotEmpty 플래그를 false로 설정
                            if (idx == 4 && !Pattern.compile(REGEX_PATTERN).matcher(td.text()).matches()) {
                                isEmailNotEmpty = false;
                            }
                            oneStaffInfo[idx] = td.text();
                        }
                        ++idx;
                    }

                    if (isEmailNotEmpty) {
                        result.add(oneStaffInfo);
                    }
                }

                break;
            }
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            throw new InternalLogicException(ErrorCode.STAFF_SCRAPER_CANNOT_PARSE, e);
        }

        return result;
    }
}
