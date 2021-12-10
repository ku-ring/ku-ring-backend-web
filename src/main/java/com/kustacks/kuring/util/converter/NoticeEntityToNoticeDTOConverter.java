package com.kustacks.kuring.util.converter;

import com.kustacks.kuring.controller.dto.NoticeDTO;
import com.kustacks.kuring.domain.notice.Notice;
import org.springframework.stereotype.Component;

@Component
public class NoticeEntityToNoticeDTOConverter implements DTOConverter {

    @Override
    public Object convert(Object target) {

        Notice notice = (Notice) target;
        return NoticeDTO.builder()
                .articleId(notice.getArticleId())
                .postedDate(notice.getPostedDate())
                .subject(notice.getSubject())
                .categoryName(notice.getCategory().getName())
                .build();
    }
}
