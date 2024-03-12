package com.kustacks.kuring.worker.update.notice.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommonNoticeFormatDto {

    private String articleId;

    private String updatedDate;

    private String subject;

    private String postedDate;

    private String fullUrl;

    private Boolean important;

    @Builder
    private CommonNoticeFormatDto(
            String articleId, String updatedDate, String subject,
            String postedDate, String fullUrl, Boolean important
    ) {
        this.articleId = articleId;
        this.updatedDate = updatedDate;
        this.subject = subject;
        this.postedDate = postedDate;
        this.fullUrl = fullUrl;
        this.important = important;
    }
}
