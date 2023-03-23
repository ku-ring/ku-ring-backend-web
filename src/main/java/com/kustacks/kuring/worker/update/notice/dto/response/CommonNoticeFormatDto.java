package com.kustacks.kuring.worker.update.notice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonNoticeFormatDto {

    private String articleId;

    private String updatedDate;

    private String subject;

    @Setter
    private String postedDate;

    @Setter
    private String baseUrl;

    @Setter
    private String fullUrl;

    public boolean isEquals(CommonNoticeFormatDto c) {
        return Objects.equals(this.articleId, c.articleId) &&
                Objects.equals(this.postedDate, c.postedDate) &&
                Objects.equals(this.updatedDate, c.updatedDate) &&
                Objects.equals(this.subject, c.subject);
    }
}
