package com.kustacks.kuring.admin.adapter.in.web.dto;

import com.kustacks.kuring.report.application.port.in.dto.AdminReportsResult;
import org.springframework.data.domain.Page;

import java.util.List;

public record AdminReportListResponse(
        List<AdminReportsResult> reports,
        boolean hasNext,
        long totalElements,
        int totalPages
) {

    public static AdminReportListResponse from(Page<AdminReportsResult> page) {
        return new AdminReportListResponse(
                page.getContent(),
                page.hasNext(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}


