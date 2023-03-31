package com.kustacks.kuring.worker.scrap;

import com.kustacks.kuring.worker.scrap.parser.notice.LatestPageNoticeHtmlParser;
import com.kustacks.kuring.worker.scrap.parser.notice.LatestPageNoticeHtmlParserTwo;
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

class NoticeHtmlParserTest {

    @DisplayName("LatestPageNoticeHtmlParser 테스트")
    @Test
    void computer_science_document_parsing_success() throws IOException {
        // given
        Document doc = Jsoup.parse(loadHtmlFile("src/test/resources/notice/cse.html"));

        // when
        RowsDto rowsDto = new LatestPageNoticeHtmlParser().parse(doc);
        List<CommonNoticeFormatDto> important = rowsDto.buildImportantRowList("important");
        List<CommonNoticeFormatDto> normal = rowsDto.buildNormalRowList("normal");

        // then
        assertAll(
                () -> assertThat(important.size()).isEqualTo(12),
                () -> assertThat(normal.size()).isEqualTo(12)
        );
    }

    @DisplayName("LatestPageNoticeHtmlParserTwo 테스트")
    @Test
    void k_beauty_document_parsing_success() throws IOException {
        // given
        Document doc = Jsoup.parse(loadHtmlFile("src/test/resources/notice/kbeauty.html"));

        // when
        RowsDto rowsDto = new LatestPageNoticeHtmlParserTwo().parse(doc);
        List<CommonNoticeFormatDto> important = rowsDto.buildImportantRowList("important");
        List<CommonNoticeFormatDto> normal = rowsDto.buildNormalRowList("normal");

        // then
        assertAll(
                () -> assertThat(important.size()).isEqualTo(28),
                () -> assertThat(normal.size()).isEqualTo(12)
        );
    }

    public static String loadHtmlFile(String filePath) throws IOException {
        Path path = Path.of(filePath);
        byte[] fileBytes = Files.readAllBytes(path);
        return new String(fileBytes, StandardCharsets.UTF_8);
    }
}
