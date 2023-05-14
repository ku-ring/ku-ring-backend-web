package com.kustacks.kuring.common.utils.converter;

import com.kustacks.kuring.worker.update.notice.dto.response.CommonNoticeFormatDto;
import com.kustacks.kuring.worker.update.notice.dto.response.KuisNoticeDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KuisNoticeDtoToCommonFormatDtoConverter implements DtoConverter {

    @Value("${notice.normal-base-url}")
    private String normalBaseUrl;

    @Override
    public Object convert(Object target) {

        KuisNoticeDto kuisNoticeDto = (KuisNoticeDto) target;
        return CommonNoticeFormatDto.builder()
                .articleId(kuisNoticeDto.getArticleId())
                .postedDate(kuisNoticeDto.getPostedDate())
                .updatedDate(null)
                .subject(kuisNoticeDto.getSubject())
                .fullUrl(normalBaseUrl + "?id=" + kuisNoticeDto.getArticleId())
                .build();
    }
}
