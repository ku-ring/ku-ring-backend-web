package com.kustacks.kuring.report.adapter.in.web.dto;

public record ReportRequest(
        Long targetId,
        String reportType,
        String content
) {
}
