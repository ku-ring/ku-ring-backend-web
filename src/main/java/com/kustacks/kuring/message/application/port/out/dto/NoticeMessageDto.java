package com.kustacks.kuring.message.application.port.out.dto;

import com.kustacks.kuring.notice.domain.*;
import lombok.*;
import org.springframework.util.Assert;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeMessageDto {

    private Long id;

    private String type;

    private String articleId;

    private String postedDate;

    private String subject;

    private String category;

    private String categoryKorName;

    private String baseUrl;

    @Builder
    private NoticeMessageDto(Long id, String articleId, String postedDate, String subject, String category, String categoryKorName, String baseUrl) {
        Assert.notNull(id, "id must not be empty");
        Assert.notNull(articleId, "articleId must not be null");
        Assert.notNull(postedDate, "postedDate must not be null");
        Assert.notNull(subject, "subject must not be null");
        Assert.notNull(category, "category must not be null");
        Assert.notNull(categoryKorName, "categoryKorName must not be null");
        Assert.notNull(baseUrl, "baseUrl must not be null");

        this.type = "notice";
        this.id = id;
        this.articleId = articleId;
        this.postedDate = postedDate;
        this.subject = subject;
        this.category = category;
        this.categoryKorName = categoryKorName;
        this.baseUrl = baseUrl;
    }

    public static NoticeMessageDto from(Notice notice) {
        return NoticeMessageDto.builder()
                .id(notice.getId())
                .articleId(notice.getArticleId())
                .postedDate(notice.getPostedDate())
                .subject(notice.getSubject())
                .category(notice.getCategoryName())
                .categoryKorName(notice.getCategoryKoreaName())
                .baseUrl(notice.getUrl())
                .build();
    }

    public static NoticeMessageDto from(DepartmentNotice departmentNotice) {
        return NoticeMessageDto.builder()
                .id(departmentNotice.getId())
                .articleId(departmentNotice.getArticleId())
                .postedDate(departmentNotice.getPostedDate())
                .subject(departmentNotice.getSubject())
                .category(departmentNotice.getDepartmentName())
                .categoryKorName(departmentNotice.getDepartmentKorName())
                .baseUrl(departmentNotice.getUrl())
                .build();
    }
}

