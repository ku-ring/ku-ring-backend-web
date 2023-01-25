package com.kustacks.kuring.common.utils.converter;

import com.kustacks.kuring.common.dto.NoticeMessageDto;
import com.kustacks.kuring.notice.domain.Notice;
import com.kustacks.kuring.kuapi.CategoryName;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NoticeEntityToNoticeMessageDTOConverter implements DTOConverter {

    @Value("${notice.normal-base-url}")
    private String normalBaseUrl;

    @Value("${notice.library-base-url}")
    private String libraryBaseUrl;

    @Override
    public Object convert(Object target) {
        Notice notice = (Notice) target;
        return NoticeMessageDto.builder()
                .articleId(notice.getArticleId())
                .postedDate(notice.getPostedDate())
                .subject(notice.getSubject())
                .category(notice.getCategory().getName())
                .baseUrl(CategoryName.LIBRARY.getName().equals(notice.getCategory().getName()) ? libraryBaseUrl : normalBaseUrl)
                .build();
    }
}
