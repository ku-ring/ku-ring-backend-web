package com.kustacks.kuring.common.utils.converter;

import com.kustacks.kuring.worker.update.notice.dto.response.CommonNoticeFormatDto;
import com.kustacks.kuring.worker.update.notice.dto.response.LibraryNoticeDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LibraryNoticeDtoToCommonFormatDtoConverter implements DtoConverter {

    @Value("${notice.library-base-url}")
    private String libraryBaseUrl;

    @Override
    public Object convert(Object target) {

        LibraryNoticeDto libraryNoticeDto = (LibraryNoticeDto) target;
        return CommonNoticeFormatDto.builder()
                .articleId(libraryNoticeDto.getId())
                .postedDate(libraryNoticeDto.getDateCreated())
                .updatedDate(libraryNoticeDto.getLastUpdated())
                .subject(libraryNoticeDto.getTitle())
                .fullUrl(libraryBaseUrl + "/" + libraryNoticeDto.getId())
                .viewCount(parseHitCount(libraryNoticeDto.getHitCnt()))
                .build();
    }

    private Integer parseHitCount(String hitCnt) {
        if (hitCnt == null || hitCnt.isBlank()) {
            return null;
        }

        try {
            return Integer.valueOf(hitCnt.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
