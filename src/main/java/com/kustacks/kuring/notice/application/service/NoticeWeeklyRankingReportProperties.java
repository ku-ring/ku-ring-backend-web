package com.kustacks.kuring.notice.application.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "notice.weekly-ranking-report")
public record NoticeWeeklyRankingReportProperties(
        List<String> recipients
) {
    public NoticeWeeklyRankingReportProperties {
        recipients = recipients == null ? List.of() : recipients.stream()
                .map(String::trim)
                .filter(recipient -> !recipient.isBlank())
                .toList();
    }

    public boolean hasRecipients() {
        return !recipients.isEmpty();
    }
}
