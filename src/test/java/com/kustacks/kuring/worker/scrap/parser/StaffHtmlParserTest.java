package com.kustacks.kuring.worker.scrap.parser;

import com.kustacks.kuring.worker.scrap.parser.staff.LivingAndCommunicationDesignStaffHtmlParser;
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
import static org.junit.jupiter.api.Assertions.assertAll;

public class StaffHtmlParserTest {

    @DisplayName("리딩 디자인 학과의 교수진 정보를 파싱한다.")
    @Test
    void LivingDesignHtmlParserTwo() throws IOException {
        // given
        Document doc = Jsoup.parse(loadHtmlFile("src/test/resources/staff/livingdesign.html"));

        // when
        List<String[]> parseResults = new LivingAndCommunicationDesignStaffHtmlParser().parse(doc);

        // then
        assertAll(
                () -> assertThat(parseResults).hasSize(5)
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
                () -> assertThat(parseResults).hasSize(7)
        );
    }

    private static String loadHtmlFile(String filePath) throws IOException {
        Path path = Path.of(filePath);
        byte[] fileBytes = Files.readAllBytes(path);
        return new String(fileBytes, StandardCharsets.UTF_8);
    }
}
