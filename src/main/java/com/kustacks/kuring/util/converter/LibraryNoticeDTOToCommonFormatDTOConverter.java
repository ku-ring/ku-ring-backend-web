package com.kustacks.kuring.util.converter;

import com.kustacks.kuring.kuapi.notice.dto.response.CommonNoticeFormatDTO;
import com.kustacks.kuring.kuapi.notice.dto.response.LibraryNoticeDTO;
import org.springframework.stereotype.Component;

@Component
public class LibraryNoticeDTOToCommonFormatDTOConverter implements DTOConverter {

    @Override
    public Object convert(Object target) {

        LibraryNoticeDTO libraryNoticeDTO = (LibraryNoticeDTO) target;
        return CommonNoticeFormatDTO.builder()
                .articleId(libraryNoticeDTO.getId())
                .postedDate(libraryNoticeDTO.getDateCreated())
                .updatedDate(libraryNoticeDTO.getLastUpdated())
                .subject(libraryNoticeDTO.getTitle())
                .build();
    }
}
