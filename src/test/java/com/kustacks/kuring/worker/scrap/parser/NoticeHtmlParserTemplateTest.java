package com.kustacks.kuring.worker.scrap.parser;

import com.kustacks.kuring.worker.scrap.parser.notice.LatestPageNoticeHtmlParser;
import com.kustacks.kuring.worker.scrap.parser.notice.LatestPageNoticeHtmlParserTwo;
import com.kustacks.kuring.worker.scrap.parser.notice.RealEstateNoticeHtmlParser;
import com.kustacks.kuring.worker.scrap.parser.notice.RowsDto;
import com.kustacks.kuring.worker.update.notice.dto.response.CommonNoticeFormatDto;
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

class NoticeHtmlParserTemplateTest {

    @DisplayName("오래된 학과의 홈페이지 공지를 분석한다")
    @Test
    void LatestPageNoticeHtmlParser() throws IOException {
        // given
        Document doc = Jsoup.parse(loadHtmlFile("src/test/resources/notice/cse.html"));

        // when
        RowsDto rowsDto = new LatestPageNoticeHtmlParser().parse(doc);
        List<CommonNoticeFormatDto> important = rowsDto.buildImportantRowList("important");
        List<CommonNoticeFormatDto> normal = rowsDto.buildNormalRowList("normal");

        // then
        assertAll(
                () -> assertThat(important).hasSize(12),
                () -> assertThat(normal).hasSize(12)
        );
    }

    @DisplayName("신규 개편된 학과의 홈페이지 공지를 분석한다")
    @Test
    void LatestPageNoticeHtmlParserTwo() throws IOException {
        // given
        Document doc = Jsoup.parse(loadHtmlFile("src/test/resources/notice/kbeauty.html"));

        // when
        RowsDto rowsDto = new LatestPageNoticeHtmlParserTwo().parse(doc);
        List<CommonNoticeFormatDto> important = rowsDto.buildImportantRowList("important");
        List<CommonNoticeFormatDto> normal = rowsDto.buildNormalRowList("normal");

        // then
        assertAll(
                () -> assertThat(important).hasSize(28),
                () -> assertThat(normal).hasSize(12)
        );
    }

    @DisplayName("부동산 학과의 경우 별도의 HTML 파서를 사용하여 공지를 분석한다")
    @Test
    void RealEstateNoticeHtmlParser() throws IOException {
        // given
        Document doc = Jsoup.parse(loadHtmlFile("src/test/resources/notice/realestate.html"));

        // when
        RowsDto rowsDto = new RealEstateNoticeHtmlParser().parse(doc);
        List<CommonNoticeFormatDto> important = rowsDto.buildImportantRowList("important");
        List<CommonNoticeFormatDto> normal = rowsDto.buildNormalRowList("normal");

        // then
        assertAll(
                () -> assertThat(important).hasSize(0),
                () -> assertThat(normal).hasSize(15)
        );
    }

    public static String loadHtmlFile(String filePath) throws IOException {
        Path path = Path.of(filePath);
        byte[] fileBytes = Files.readAllBytes(path);
        return new String(fileBytes, StandardCharsets.UTF_8);
    }
}
