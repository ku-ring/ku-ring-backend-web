package com.kustacks.kuring.kuapi.notice.dto.response;

import lombok.*;

import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommonNoticeFormatDTO {

    private String articleId;

    private String postedDate;

    private String updatedDate;

    private String subject;

    public boolean isEquals(CommonNoticeFormatDTO c) {
        return Objects.equals(this.articleId, c.articleId) &&
                Objects.equals(this.postedDate, c.postedDate) &&
                Objects.equals(this.updatedDate, c.updatedDate) &&
                Objects.equals(this.subject, c.subject);
    }

    public void setPostedDate(String postedDate) {
        this.postedDate = postedDate;
    }
}
