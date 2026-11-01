package com.kustacks.kuring.worker.parser.notice;

import com.kustacks.kuring.worker.update.notice.dto.response.CommonNoticeFormatDto;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RowsDto {

    private static final int VIEW_COUNT_INDEX = 3;
    private static final String NON_DIGIT_REGEX = "[^0-9]";

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
                        .viewCount(parseViewCount(row))
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
                        .viewCount(parseViewCount(row))
                        .build())
                .collect(Collectors.toList());
    }

    // 조회수 컬럼을 제공하지 않는 파서도 있으므로, 값이 없거나 숫자가 아니면 null로 처리한다
    private static Integer parseViewCount(String[] row) {
        if (row.length <= VIEW_COUNT_INDEX) {
            return null;
        }

        String digits = row[VIEW_COUNT_INDEX].replaceAll(NON_DIGIT_REGEX, "");
        if (digits.isEmpty()) {
            return null;
        }

        try {
            return Integer.valueOf(digits);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
