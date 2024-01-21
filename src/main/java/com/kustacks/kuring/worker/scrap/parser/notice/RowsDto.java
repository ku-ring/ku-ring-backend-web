package com.kustacks.kuring.worker.scrap.parser.notice;

import com.kustacks.kuring.worker.update.notice.dto.response.CommonNoticeFormatDto;

import java.util.List;
import java.util.stream.Collectors;

public class RowsDto {

    private final List<String[]> importantRowList;
    private final List<String[]> normalRowList;

    public RowsDto(List<String[]> importantRowList, List<String[]> normalRowList) {
        this.importantRowList = importantRowList;
        this.normalRowList = normalRowList;
    }

    public List<CommonNoticeFormatDto> buildImportantRowList(String viewUrl) {
        return importantRowList.stream()
                .map(row -> CommonNoticeFormatDto
                        .builder()
                        .articleId(row[0])
                        .postedDate(row[1])
                        .subject(row[2])
                        .fullUrl(viewUrl + row[0])
                        .important(true)
                        .build())
                .toList();
    }

    public List<CommonNoticeFormatDto> buildNormalRowList(String viewUrl) {
        return normalRowList.stream()
                .map(row -> CommonNoticeFormatDto
                        .builder()
                        .articleId(row[0])
                        .postedDate(row[1])
                        .subject(row[2])
                        .fullUrl(viewUrl + row[0])
                        .important(false)
                        .build())
                .toList();
    }
}
