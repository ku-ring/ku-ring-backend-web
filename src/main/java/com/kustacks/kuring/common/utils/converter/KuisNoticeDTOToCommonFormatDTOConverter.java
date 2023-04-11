package com.kustacks.kuring.common.utils.converter;

import com.kustacks.kuring.worker.update.notice.dto.response.CommonNoticeFormatDto;
import com.kustacks.kuring.worker.update.notice.dto.response.KuisNoticeDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KuisNoticeDTOToCommonFormatDTOConverter implements DTOConverter {

    @Value("${notice.normal-base-url}")
    private String normalBaseUrl;

    @Override
    public Object convert(Object target) {

        KuisNoticeDTO kuisNoticeDTO = (KuisNoticeDTO) target;
        return CommonNoticeFormatDto.builder()
                .articleId(kuisNoticeDTO.getArticleId())
                .postedDate(kuisNoticeDTO.getPostedDate())
                .updatedDate(null)
                .subject(kuisNoticeDTO.getSubject())
                .fullUrl(normalBaseUrl + "?id=" + kuisNoticeDTO.getArticleId())
                .build();
    }
}
