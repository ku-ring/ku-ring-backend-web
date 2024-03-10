package com.kustacks.kuring.worker.parser;

import com.kustacks.kuring.worker.parser.staff.EachDeptStaffHtmlParser;
import com.kustacks.kuring.worker.parser.staff.LivingAndCommunicationDesignStaffHtmlParser;
import com.kustacks.kuring.worker.parser.staff.RealEstateStaffHtmlParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;

class StaffHtmlParserTemplateTest {

    @DisplayName("리빙 디자인 학과의 교수진 정보를 파싱한다.")
    @Test
    void LivingDesignHtmlParserTwo() throws IOException {
        // given
        Document doc = Jsoup.parse(loadHtmlFile("src/test/resources/staff/livingdesign.html"));

        // when
        List<String[]> parseResults = new LivingAndCommunicationDesignStaffHtmlParser().parse(doc);

        // then
        assertAll(
                () -> assertThat(parseResults).hasSize(5),
                () -> assertThat(parseResults)
                        .extracting(arr -> arr[0], arr -> arr[1], arr -> arr[2], arr -> arr[3], arr -> arr[4])
                        .containsExactlyInAnyOrder(
                                tuple("김선미", "텍스타일디자인", "예술문화관 507호", "02-450-3790", "cemi@konkuk.ac.kr"),
                                tuple("이필하", "섬유미술", "예술문화관 504호", "02-2049-6015", "imphil@konkuk.ac.kr"),
                                tuple("김성달", "텍스타일디자인", "예술문화관 505호", "02-450-3732", "dahlkim@naver.com"),
                                tuple("이하린", "도자공예", "예술문화관 506호", "02-450-3796", "fredmizer@naver.com"),
                                tuple("황선욱", "금속공예", "예술문화관 519호", "02-2049-6032", "renerary@hanmail.net")
                        )
        );
    }

    @DisplayName("커뮤니케이션 디자인 학과의 교수진 정보를 파싱한다.")
    @Test
    void CommunicationDesignHtmlParserTwo() throws IOException {
        // given
        Document doc = Jsoup.parse(loadHtmlFile("src/test/resources/staff/communicationdesign.html"));

        // when
        List<String[]> parseResults = new LivingAndCommunicationDesignStaffHtmlParser().parse(doc);

        // then
        assertAll(
                () -> assertThat(parseResults).hasSize(7),
                () -> assertThat(parseResults)
                        .extracting(arr -> arr[0], arr -> arr[1], arr -> arr[2], arr -> arr[3], arr -> arr[4])
                        .containsExactlyInAnyOrder(
                                tuple("박성완", "드로잉", "예술문화관 811호", "02-450-3787", "swpark@konkuk.ac.kr"),
                                tuple("김병진", "시각디자인", "예술문화관 810호", "02-450-3788", "turbokbj@hanmail.net"),
                                tuple("유우종", "멀티미디어디자인", "예술문화관 809호", "02-450-4115", "yoowoojong@hotmail.com"),
                                tuple("김지윤", "디지털미디어디자인", "예술문화관 808호", "02-450-3763", "joonkim@konkuk.ac.kr"),
                                tuple("한창호", "시각디자인", "예술문화관 807호", "02-2049-6057", "hann@konkuk.ac.kr"),
                                tuple("최병일", "멀티미디어디자인", "예술문화관 806호", "02-2049-6056", "redhorse@konkuk.ac.kr"),
                                tuple("조혜영", "디자인 한국학/역사문화이론", "예술문화관 804호", "02-450-4297", "joahe@konkuk.ac.kr")
                        )
        );
    }

    @DisplayName("부동 학과의 교수진 정보를 파싱한다.")
    @Test
    void RealEstateStaffHtmlParser() throws IOException {
        // given
        Document doc = Jsoup.parse(loadHtmlFile("src/test/resources/staff/realestate.html"));

        // when
        List<String[]> parseResults = new RealEstateStaffHtmlParser().parse(doc);

        // then
        assertThat(parseResults).hasSize(11);
    }

    @DisplayName("컴퓨터공학과의 교수진 정보를 파싱한다.")
    @Test
    void StaffEachDeptHtmlParser() throws IOException {
        // given
        Document doc = Jsoup.parse(loadHtmlFile("src/test/resources/staff/computer.html"));

        // when
        List<String[]> parseResults = new EachDeptStaffHtmlParser().parse(doc);

        // then
        assertAll(
                () -> assertThat(parseResults).hasSize(5),
                () -> assertThat(parseResults)
                        .extracting(arr -> arr[0], arr -> arr[1], arr -> arr[2], arr -> arr[3], arr -> arr[4])
                        .containsExactlyInAnyOrder(
                                tuple("김기천 ( Keecheon Kim )", "Computer Communications and Mobile Computing", "공학관 C동 385-2호 / 신공학관 1205호", "02-450-3518", "kckim@konkuk.ac.kr"),
                                tuple("김두현 ( Doohyun Kim )", "Embedded &amp; Intelligent Computing", "공학관 C동 483호 / 신공학관 1004호", "02-2049-6044", "doohyun@konkuk.ac.kr"),
                                tuple("김성렬 ( Sung-Ryul Kim )", "Cryptography and System Security", "공학관 483-2호", "02-450-4134", "kimsr@konkuk.ac.kr"),
                                tuple("김욱희", "Database Systems, Storage Systems", "공학관 C동 422호 / 신공학관 1217호", "02-450-3493", "wookhee@konkuk.ac.kr"),
                                tuple("김은이 ( Eun Yi Kim )", "인공지능, 컴퓨터비전", "공학관 483-1호", "02-450-4135", "eykim@konkuk.ac.kr")
                        )
        );
    }

    private static String loadHtmlFile(String filePath) throws IOException {
        Path path = Path.of(filePath);
        byte[] fileBytes = Files.readAllBytes(path);
        return new String(fileBytes, StandardCharsets.UTF_8);
    }
}
