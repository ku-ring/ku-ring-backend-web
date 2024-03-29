package com.kustacks.kuring.worker.parser.notice;

import com.kustacks.kuring.worker.update.notice.dto.response.CommonNoticeFormatDto;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
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
        Collections.reverse(importantRowList);
        return importantRowList.stream()
                .map(row -> CommonNoticeFormatDto
                        .builder()
                        .articleId(row[0])
                        .postedDate(row[1])
                        .subject(row[2])
                        .fullUrl(
                                UriComponentsBuilder.fromUriString(viewUrl)
                                        .buildAndExpand(row[0])
                                        .toUriString()
                        )
                        .important(true)
                        .build())
                .collect(Collectors.toList());
    }

    public List<CommonNoticeFormatDto> buildNormalRowList(String viewUrl) {
        Collections.reverse(normalRowList);
        return normalRowList.stream()
                .map(row -> CommonNoticeFormatDto
                        .builder()
                        .articleId(row[0])
                        .postedDate(row[1])
                        .subject(row[2])
                        .fullUrl(
                                UriComponentsBuilder.fromUriString(viewUrl)
                                        .buildAndExpand(row[0])
                                        .toUriString()
                        )
                        .important(false)
                        .build())
                .collect(Collectors.toList());
    }
}
