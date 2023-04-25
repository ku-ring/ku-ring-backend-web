package com.kustacks.kuring.common.dto;

import com.kustacks.kuring.notice.domain.DepartmentNotice;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeMessageDto {

    private String type;

    private String articleId;

    private String postedDate;

    private String subject;

    private String category;

    private String baseUrl;

    @Builder
    private NoticeMessageDto(String articleId, String postedDate, String subject, String category, String baseUrl) {
        Assert.notNull(articleId, "articleId must not be null");
        Assert.notNull(postedDate, "postedDate must not be null");
        Assert.notNull(subject, "subject must not be null");
        Assert.notNull(category, "category must not be null");
        Assert.notNull(baseUrl, "baseUrl must not be null");

        this.type = "notice";
        this.articleId = articleId;
        this.postedDate = postedDate;
        this.subject = subject;
        this.category = category;
        this.baseUrl = baseUrl;
    }

    public static NoticeMessageDto from(DepartmentNotice departmentNotice) {
        return NoticeMessageDto.builder()
                .articleId(departmentNotice.getArticleId())
                .postedDate(departmentNotice.getPostedDate())
                .subject(departmentNotice.getSubject())
                .category(departmentNotice.getDepartmentName().getName())
                .baseUrl(departmentNotice.getUrl().getValue())
                .build();
    }
}

