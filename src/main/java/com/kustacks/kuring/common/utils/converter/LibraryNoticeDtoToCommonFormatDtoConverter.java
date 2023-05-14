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
                .build();
    }
}
