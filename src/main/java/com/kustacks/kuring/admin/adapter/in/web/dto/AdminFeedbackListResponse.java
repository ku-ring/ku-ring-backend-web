package com.kustacks.kuring.admin.adapter.in.web.dto;

import com.kustacks.kuring.user.application.port.in.dto.AdminFeedbacksResult;
import org.springframework.data.domain.Page;

import java.util.List;

public record AdminFeedbackListResponse(
        List<AdminFeedbacksResult> feedbacks,
        boolean hasNext,
        long totalElements,
        int totalPages
) {

    public static AdminFeedbackListResponse from(Page<AdminFeedbacksResult> page) {
        return new AdminFeedbackListResponse(
                page.getContent(),
                page.hasNext(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}


