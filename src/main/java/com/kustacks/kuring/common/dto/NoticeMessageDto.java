package com.kustacks.kuring.common.dto;

import com.kustacks.kuring.notice.domain.DepartmentNotice;
import com.kustacks.kuring.notice.domain.Notice;
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

    private String categoryKorName;

    private String url;

    @Builder
    private NoticeMessageDto(String articleId, String postedDate, String subject, String category, String categoryKorName, String url) {
        Assert.notNull(articleId, "articleId must not be null");
        Assert.notNull(postedDate, "postedDate must not be null");
        Assert.notNull(subject, "subject must not be null");
        Assert.notNull(category, "category must not be null");
        Assert.notNull(categoryKorName, "categoryKorName must not be null");
        Assert.notNull(url, "url must not be null");

        this.type = "notice";
        this.articleId = articleId;
        this.postedDate = postedDate;
        this.subject = subject;
        this.category = category;
        this.categoryKorName = categoryKorName;
        this.url = url;
    }

    public static NoticeMessageDto from(Notice notice) {
        return NoticeMessageDto.builder()
                .articleId(notice.getArticleId())
                .postedDate(notice.getPostedDate())
                .subject(notice.getSubject())
                .category(notice.getCategoryName())
                .categoryKorName(notice.getCategoryKoreaName())
                .url(notice.getUrl())
                .build();
    }

    public static NoticeMessageDto from(DepartmentNotice departmentNotice) {
        return NoticeMessageDto.builder()
                .articleId(departmentNotice.getArticleId())
                .postedDate(departmentNotice.getPostedDate())
                .subject(departmentNotice.getSubject())
                .category(departmentNotice.getDepartmentName())
                .categoryKorName(departmentNotice.getDepartmentKorName())
                .url(departmentNotice.getUrl())
                .build();
    }
}

