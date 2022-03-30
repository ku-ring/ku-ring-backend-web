package com.kustacks.kuring.util.converter;

import com.kustacks.kuring.controller.dto.NoticeDTO;
import com.kustacks.kuring.persistence.notice.Notice;
import org.springframework.stereotype.Component;

@Component
public class NoticeEntityToNoticeDTOConverter implements DTOConverter<NoticeDTO, Notice> {

    @Override
    public NoticeDTO convert(Notice target) {
        return NoticeDTO.builder()
                .articleId(target.getArticleId())
                .postedDate(target.getPostedDate())
                .subject(target.getSubject())
                .categoryName(target.getCategory().getName())
                .fullUrl(target.getFullUrl())
                .build();
    }
}
