package com.kustacks.kuring.worker.update.notice.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@NoArgsConstructor
public class CommonNoticeFormatDto {

    private String articleId;

    private String updatedDate;

    private String subject;

    @Setter
    private String postedDate;

    @Setter
    private String fullUrl;

    @Builder
    public CommonNoticeFormatDto(String articleId, String updatedDate, String subject, String postedDate, String fullUrl) {
        this.articleId = articleId;
        this.updatedDate = updatedDate;
        this.subject = subject;
        this.postedDate = postedDate;
        this.fullUrl = fullUrl;
    }

    public boolean isEquals(CommonNoticeFormatDto c) {
        return Objects.equals(this.articleId, c.articleId) &&
                Objects.equals(this.postedDate, c.postedDate) &&
                Objects.equals(this.updatedDate, c.updatedDate) &&
                Objects.equals(this.subject, c.subject);
    }
}
