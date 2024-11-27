package com.kustacks.kuring.worker.parser;

import com.kustacks.kuring.support.TestFileLoader;
import com.kustacks.kuring.worker.parser.staff.EachDeptStaffHtmlParser;
import com.kustacks.kuring.worker.parser.staff.RealEstateStaffHtmlParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;

public class StaffHtmlParserTemplateTest {

    @DisplayName("부동산 학과의 교수진 정보를 파싱한다.")
    @Test
    void RealEstateStaffHtmlParser() throws IOException {
        // given
        Document doc = Jsoup.parse(TestFileLoader.loadHtmlFile("src/test/resources/staff/real_estate_staff_page.html"));

        // when
        List<String[]> parseResults = new RealEstateStaffHtmlParser().parse(doc);

        // then
        assertThat(parseResults).hasSize(11);
    }

    @DisplayName("컴퓨터공학부 홈페이지의 교수진 정보를 파싱한다.")
    @Test
    public void LatestStaffEachDeptHtmlParser() throws IOException {
        // given
        Document doc = Jsoup.parse(TestFileLoader.loadHtmlFile("src/test/resources/staff/cse_staff_page.html"));

        // when
        EachDeptStaffHtmlParser parser = new EachDeptStaffHtmlParser();
        List<String[]> parseResults = parser.parse(doc);
        // then
        assertAll(
                // 전체 파싱된 교수진 수 검증
                () -> assertThat(parseResults).hasSize(23),

                // 각각의 데이터 검증
                () -> assertThat(parseResults)
                        .extracting(
                                arr -> arr[0], // 이름
                                arr -> arr[1], // 직위
                                arr -> arr[2], // 연구분야
                                arr -> arr[3], // 연구실 위치
                                arr -> arr[4], // 전화번호
                                arr -> arr[5]  // 이메일
                        )
                        .containsExactlyInAnyOrder(
                                tuple("김기천 (Keecheon Kim)", "교수", "Computer Communications and Mobile Computing", "공C385-2", "3518", "kckim@konkuk.ac.kr"),
                                tuple("김두현 (Doo Hyun Kim)", "교수", "Embedded & Intelligent Computing", "공C483", "6044", "doohyun@konkuk.ac.kr"),
                                tuple("김성열 (Sung Ryul Kim)", "교수", "Cryptography and System Security", "공C483-2", "4134", "kimsr@konkuk.ac.kr"),
                                tuple("김욱희 (Wookhee Kim)", "조교수", "Database Systems, Storage Systems", "공C422", "3493", "wookhee@konkuk.ac.kr"),
                                tuple("김은이 (Eun Yi KIm)", "교수", "인공지능, 컴퓨터비전", "공C483-1", "4135", "eykim@konkuk.ac.kr"),
                                tuple("김학수 (Kim, Harksoo)", "교수", "Natural Language Processing", "공C386-1", "3499", "nlpdrkim@konkuk.ac.kr"),
                                tuple("김형석 (HyungSeok Kim)", "교수", "Virtual Reality/Computer Graphics", "공C292-2", "4095", "hyuskim@konkuk.ac.kr"),
                                tuple("남원홍 (WONHONG NAM)", "교수", "Formal Methods", "공C293", "6128", "wnam@konkuk.ac.kr"),
                                tuple("민덕기 (Min, Dugki)", "교수", "Distributed Systems / AI(Deep (Reinforcement) Learning) / Software Architecture", "공C385", "3490", "dkmin@konkuk.ac.kr"),
                                tuple("박능수 (Neungsoo Park)", "교수", "Computer Architecture and Parallel Computing", "공C384-1", "4081", "neungsoo@konkuk.ac.kr"),
                                tuple("박소영 (Soyoung Park)", "부교수", "Cryptography", "공A1409-1", "0482", "soyoungpark@konkuk.ac.kr"),
                                tuple("신효섭 (Hyoseop Shin)", "교수", "Database Systems", "공C386-2", "6117", "hsshin@konkuk.ac.kr"),
                                tuple("오병국 (Byungkook Oh)", "조교수", "", "공C384-2", "3073", "bkoh@konkuk.ac.kr"),
                                tuple("유준범 (JUNBEOM YOO)", "교수", "Software Engineering", "공C386", "3258", "jbyoo@konkuk.ac.kr"),
                                tuple("윤경로 (Kyoungro Yoon)", "교수", "Multimedia Systems", "공C384", "4129", "yoonk@konkuk.ac.kr"),
                                tuple("이향원 (Hyang-Won Lee)", "교수", "Networked Systems and Data Science", "공C293-1", "0471", "leehw@konkuk.ac.kr"),
                                tuple("임민규 (Mingyu Lim)", "교수", "Distributed Systems", "공C292-1", "6270", "mlim@konkuk.ac.kr"),
                                tuple("임창훈 (Yim, Changhoon)", "교수", "Image Processing, Computer Vision", "공C292-3", "4016", "cyim@konkuk.ac.kr"),
                                tuple("정갑주 (KARPJOO JEONG)", "교수", "Smart Infrastructure", "공C385-1", "3510", "jeongk@konkuk.ac.kr"),
                                tuple("지정희 (Jeonghee Chi)", "조교수", "Spatiotemporal Database", "공학관 A동 1409-1호", "3350", "jhchi@konkuk.ac.kr"),
                                tuple("진현욱 (Hyun-Wook Jin)", "교수", "Operating Systems", "공C291-1", "3535", "jinh@konkuk.ac.kr"),
                                tuple("차영운 (YoungWoon Cha)", "조교수", "Extended Reality, Vision, Graphics", "공C293-2", "3509", "youngcha@konkuk.ac.kr"),
                                tuple("하영국 (Ha Young-Guk)", "교수", "Intelligent Systems and Big Data", "공C291-2", "3273", "ygha@konkuk.ac.kr")
                        )

        );
    }

}
