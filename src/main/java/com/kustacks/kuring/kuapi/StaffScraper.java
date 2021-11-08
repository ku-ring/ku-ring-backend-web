package com.kustacks.kuring.kuapi;

import com.kustacks.kuring.controller.dto.StaffDTO;
import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.error.InternalLogicException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Component
public class StaffScraper {

    public StaffScraper() {}

    public List<StaffDTO> getStaffInfo(StaffDeptInfo dept) throws IOException {

        Document document = Jsoup.connect(dept.getUrl()).get();
        List<StaffDTO> staffDTOList = null;

        // CODE SMELL?
        if(dept.getCollegeName().equals("상허생명과학대학")) {
            Element pageNumHiddenInput = document.getElementById("totalPageCount");
            int totalPageNum = Integer.parseInt(pageNumHiddenInput.val());
            int pageNum = 1; // 이미 1페이지를 받아왔으므로 2페이지부터 호출하면됨

            System.out.println(dept.getName() + " 의 페이지사이즈 = " + totalPageNum);

            staffDTOList = new LinkedList<>();

            while(true) {
                staffDTOList.addAll(getSanghuoBiologyDeptStaffInfo(document, dept.getName(), dept.getCollegeName()));
                ++pageNum;

                if(pageNum > totalPageNum) {
                    break;
                }

                document = Jsoup.connect(dept.getUrl())
                        .data("pageNum", String.valueOf(pageNum))
                        .post();
            }
        } else if(dept.getCollegeName().equals("부동산과학원")) {
            staffDTOList = getRealEstateScienceDeptStaffInfo(document, dept.getName(), dept.getCollegeName());
        } else {
            staffDTOList = getNormalDeptStaffInfo(document, dept.getName(), dept.getCollegeName());
        }

        return staffDTOList;
    }

    public void printStaffDTOList(String title, List<StaffDTO> staffDTOList) {
        System.out.println("===== " + title + " =====");
        for (StaffDTO staffDTO : staffDTOList) {
            System.out.println("이름 = " + staffDTO.getName());
            System.out.println("전공 = " + staffDTO.getMajor());
            System.out.println("랩실 = " + staffDTO.getLab());
            System.out.println("전번 = " + staffDTO.getPhone());
            System.out.println("이메일 = " + staffDTO.getEmail());
            System.out.println();
        }
    }

    private List<StaffDTO> getNormalDeptStaffInfo(Document document, String deptName, String collegeName) {

        // 테이블 추출
        Elements tables = document.getElementsByTag("table");

        // 이름, 전공, 연구실, 전화번호, 이메일 순 추출
        List<StaffDTO> staffDTOList = new LinkedList<>();
        String[] oneStaffInfo = new String[5];
        for (Element table : tables) {

            // 물리학과의 경우 테이블이 2개임. 이를 필터링하기 위한 코드
            // colgroup 태그에서 column 개수가 5가 아니면 교수 테이블이 아니라고 판단한다.
            // 만약 colgroup이 없다면, 예상치 못한 경우이므로 에러로 처리
            Elements colgroups = table.select("colgroup");
            if(colgroups.size() == 0) {
                throw new InternalLogicException(ErrorCode.STAFF_SCRAPER_TAG_NOT_EXIST);
            }

            // TODO: 컬럼 개수 6개인 학과도 있음.
            Element colgroup = colgroups.get(0);
            int colgroupSize = colgroup.childrenSize();
            if(colgroupSize != 5 && colgroupSize != 6) {
                continue;
            }

            Elements rows = table.select("tbody > tr");
            for (Element row : rows) {
                int idx = 0;
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
                    addNewKuStaffDTOToList(staffDTOList, oneStaffInfo, deptName, collegeName);
                } else {
                    // TODO: 테스트용
                    System.out.println("스킵된 교수 정보. " + deptName + " " + oneStaffInfo[0] + " 교수");
                }
            }

            break;
        }

        return staffDTOList;
    }

    private List<StaffDTO> getSanghuoBiologyDeptStaffInfo(Document document, String deptName, String collegeName) {

        List<StaffDTO> staffDTOList = new LinkedList<>();

        Element table = document.select(".photo_intro").get(0);
        Elements rows = table.getElementsByTag("dl");

        String[] oneStaffInfo = new String[5];
        for (Element row : rows) {
            Elements infos = row.getElementsByTag("dd");

            // 교수명, 직위, 세부전공, 연구실, 연락처, 이메일 순으로 파싱
            // 연구실, 연락처 정보는 없는 경우가 종종 있으므로, childNode접근 전 인덱스 체크하는 로직을 넣었음
            oneStaffInfo[0] = infos.get(0).getElementsByTag("span").get(1).text();

            String jobPosition = String.valueOf(infos.get(2).childNode(1));
            if(jobPosition.contains("명예") || jobPosition.contains("대우")) {
                // TODO: 테스트용
                System.out.println("스킵된 교수 정보. " + deptName + " " + oneStaffInfo[0] + " 교수");
                continue;
            }

            oneStaffInfo[1] = jobPosition;
            oneStaffInfo[2] = infos.get(3).childNodeSize() < 2 ? "" : String.valueOf(infos.get(3).childNode(1));
            oneStaffInfo[3] = infos.get(4).childNodeSize() < 2 ? "" : String.valueOf(infos.get(4).childNode(1));
            oneStaffInfo[4] = infos.get(5).getElementsByTag("a").get(0).text();

            addNewKuStaffDTOToList(staffDTOList, oneStaffInfo, deptName, collegeName);
        }

        return staffDTOList;
    }

    private List<StaffDTO> getRealEstateScienceDeptStaffInfo(Document document, String deptName, String collegeName) {

        List<StaffDTO> staffDTOList = new LinkedList<>();

        Element table = document.select(".sub0201_list").get(0).getElementsByTag("ul").get(0);
        Elements rows = table.getElementsByTag("li");

        String[] oneStaffInfo = new String[5];
        for (Element row : rows) {
            Element content = row.select(".con").get(0);

            oneStaffInfo[0] = content.select("dl > dt > a > strong").get(0).text();
            oneStaffInfo[1] = String.valueOf(content.select("dl > dd").get(0).childNode(4)).replaceFirst("\\s", "").trim();

            Element textMore = content.select(".text_more").get(0);

            oneStaffInfo[4] = textMore.getElementsByTag("a").get(0).text();
            oneStaffInfo[2] = String.valueOf(textMore.childNode(4)).split(":")[1].replaceFirst("\\s", "").trim();
            oneStaffInfo[3] = String.valueOf(textMore.childNode(6)).split(":")[1].replaceFirst("\\s", "").trim();

            addNewKuStaffDTOToList(staffDTOList, oneStaffInfo, deptName, collegeName);
        }

        return staffDTOList;
    }

    private void addNewKuStaffDTOToList(List<StaffDTO> staffDTOList, String[] oneStaffInfo, String deptName, String collegeName) {
        staffDTOList.add(StaffDTO.builder()
                .name(oneStaffInfo[0])
                .major(oneStaffInfo[1])
                .lab(oneStaffInfo[2])
                .phone(oneStaffInfo[3])
                .email(oneStaffInfo[4])
                .deptName(deptName)
                .collegeName(collegeName).build());
    }
}
