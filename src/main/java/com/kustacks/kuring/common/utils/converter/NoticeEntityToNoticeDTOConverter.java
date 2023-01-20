package com.kustacks.kuring.common.utils.converter;

import com.kustacks.kuring.common.dto.NoticeDTO;
import com.kustacks.kuring.notice.domain.Notice;
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
