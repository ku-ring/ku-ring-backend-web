package com.kustacks.kuring.notice.adapter.out.excel;

import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.notice.domain.NoticeWeeklyRanking;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("어댑터 : NoticeRankingExcelAdapter")
class NoticeRankingExcelAdapterTest {

    private static final LocalDate WEEK_START_DATE = LocalDate.of(2026, 10, 26);

    @DisplayName("주간 인기 공지 순위를 기간, 헤더와 함께 순위 순서대로 엑셀로 만든다")
    @Test
    void createWeeklyRankingExcel() throws IOException {
        // given
        List<NoticeWeeklyRanking> rankings = List.of(
                ranking(1, "1210289", "2026학년도 동계 계절학기 예비수강신청 안내", CategoryName.BACHELOR, 893),
                ranking(2, "1210566", "2026학년도 2학기 복지장학금 신청 안내", CategoryName.SCHOLARSHIP, 726)
        );

        // when
        byte[] excel = new NoticeRankingExcelAdapter().createWeeklyRankingExcel(WEEK_START_DATE, rankings);

        // then
        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excel))) {
            Sheet sheet = workbook.getSheet("주간 인기 공지");
            Row header = sheet.getRow(2);
            Row first = sheet.getRow(3);
            Row second = sheet.getRow(4);

            assertAll(
                    () -> assertThat(sheet.getRow(0).getCell(0).getStringCellValue())
                            .isEqualTo("주간 인기 공지 (2026-10-26 ~ 2026-11-01)"),
                    () -> assertThat(header.getCell(0).getStringCellValue()).isEqualTo("순위"),
                    () -> assertThat(header.getCell(4).getStringCellValue()).isEqualTo("주간 조회수"),
                    () -> assertThat(first.getCell(0).getNumericCellValue()).isEqualTo(1),
                    () -> assertThat(first.getCell(1).getStringCellValue()).isEqualTo("학사"),
                    () -> assertThat(first.getCell(2).getStringCellValue()).isEqualTo("2026학년도 동계 계절학기 예비수강신청 안내"),
                    () -> assertThat(first.getCell(3).getLocalDateTimeCellValue().toLocalDate()).isEqualTo(LocalDate.of(2026, 10, 20)),
                    () -> assertThat(first.getCell(4).getNumericCellValue()).isEqualTo(893),
                    () -> assertThat(first.getCell(5).getStringCellValue()).isEqualTo("https://www.example.com/1210289"),
                    () -> assertThat(second.getCell(1).getStringCellValue()).isEqualTo("장학"),
                    () -> assertThat(sheet.getLastRowNum()).isEqualTo(4)
            );
        }
    }

    private static NoticeWeeklyRanking ranking(int ranking, String articleId, String subject, CategoryName categoryName, int viewCount) {
        return NoticeWeeklyRanking.builder()
                .weekStartDate(WEEK_START_DATE)
                .ranking(ranking)
                .noticeId((long) ranking)
                .articleId(articleId)
                .subject(subject)
                .categoryName(categoryName)
                .postedDate(LocalDateTime.of(2026, 10, 20, 9, 0))
                .url("https://www.example.com/" + articleId)
                .viewCount(viewCount)
                .build();
    }
}
