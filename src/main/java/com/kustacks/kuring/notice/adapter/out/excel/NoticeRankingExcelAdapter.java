package com.kustacks.kuring.notice.adapter.out.excel;

import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.notice.application.port.out.NoticeRankingExcelPort;
import com.kustacks.kuring.notice.domain.NoticeWeeklyRanking;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Component
public class NoticeRankingExcelAdapter implements NoticeRankingExcelPort {

    private static final String SHEET_NAME = "주간 인기 공지";
    private static final String FONT_NAME = "Arial";
    private static final String[] HEADERS = {"순위", "카테고리", "제목", "게시일", "주간 조회수", "URL"};
    private static final int[] COLUMN_WIDTHS = {6, 10, 70, 12, 12, 70};
    private static final int TITLE_ROW_INDEX = 0;
    private static final int HEADER_ROW_INDEX = 2;
    private static final byte[] HEADER_COLOR = {(byte) 0x1F, (byte) 0x4E, (byte) 0x78};

    @Override
    public byte[] createWeeklyRankingExcel(LocalDate weekStartDate, List<NoticeWeeklyRanking> rankings) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(SHEET_NAME);
            Styles styles = new Styles(workbook);

            Cell titleCell = sheet.createRow(TITLE_ROW_INDEX).createCell(0);
            titleCell.setCellValue("주간 인기 공지 (" + weekStartDate + " ~ " + weekStartDate.plusDays(6) + ")");
            titleCell.setCellStyle(styles.title);

            Row headerRow = sheet.createRow(HEADER_ROW_INDEX);
            for (int column = 0; column < HEADERS.length; column++) {
                Cell cell = headerRow.createCell(column);
                cell.setCellValue(HEADERS[column]);
                cell.setCellStyle(styles.header);
                sheet.setColumnWidth(column, COLUMN_WIDTHS[column] * 256);
            }

            int rowIndex = HEADER_ROW_INDEX + 1;
            for (NoticeWeeklyRanking ranking : rankings) {
                writeRankingRow(sheet.createRow(rowIndex++), ranking, styles);
            }

            sheet.createFreezePane(0, HEADER_ROW_INDEX + 1);
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new InternalLogicException(ErrorCode.NOTICE_RANKING_REPORT_CANNOT_CREATE_EXCEL, e);
        }
    }

    private void writeRankingRow(Row row, NoticeWeeklyRanking ranking, Styles styles) {
        Cell rankingCell = row.createCell(0);
        rankingCell.setCellValue(ranking.getRanking());
        rankingCell.setCellStyle(styles.center);

        Cell categoryCell = row.createCell(1);
        categoryCell.setCellValue(ranking.getCategoryName().getKorName());
        categoryCell.setCellStyle(styles.body);

        Cell subjectCell = row.createCell(2);
        subjectCell.setCellValue(ranking.getSubject());
        subjectCell.setCellStyle(styles.body);

        Cell postedDateCell = row.createCell(3);
        postedDateCell.setCellValue(ranking.getPostedDate().toLocalDate());
        postedDateCell.setCellStyle(styles.date);

        Cell viewCountCell = row.createCell(4);
        viewCountCell.setCellValue(ranking.getViewCount());
        viewCountCell.setCellStyle(styles.number);

        Cell urlCell = row.createCell(5);
        urlCell.setCellValue(ranking.getUrl());
        urlCell.setCellStyle(styles.body);
    }

    private static class Styles {

        private final CellStyle title;
        private final CellStyle header;
        private final CellStyle body;
        private final CellStyle center;
        private final CellStyle date;
        private final CellStyle number;

        private Styles(XSSFWorkbook workbook) {
            XSSFFont bodyFont = createFont(workbook, (short) 10, false);
            XSSFFont titleFont = createFont(workbook, (short) 14, true);
            XSSFFont headerFont = createFont(workbook, (short) 10, true);
            headerFont.setColor(new XSSFColor(new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF}, null));

            this.title = workbook.createCellStyle();
            this.title.setFont(titleFont);

            XSSFCellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(new XSSFColor(HEADER_COLOR, null));
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            this.header = headerStyle;

            this.body = workbook.createCellStyle();
            this.body.setFont(bodyFont);

            this.center = workbook.createCellStyle();
            this.center.setFont(bodyFont);
            this.center.setAlignment(HorizontalAlignment.CENTER);

            this.date = workbook.createCellStyle();
            this.date.setFont(bodyFont);
            this.date.setDataFormat(workbook.createDataFormat().getFormat("yyyy-mm-dd"));

            this.number = workbook.createCellStyle();
            this.number.setFont(bodyFont);
            this.number.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
        }

        private static XSSFFont createFont(XSSFWorkbook workbook, short size, boolean bold) {
            XSSFFont font = workbook.createFont();
            font.setFontName(FONT_NAME);
            font.setFontHeightInPoints(size);
            font.setBold(bold);
            return font;
        }
    }
}
