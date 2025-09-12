package com.kustacks.kuring.admin.adapter.in.web.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record AdminAlertListResponse(
        List<AdminAlertResponse> alerts,
        boolean hasNext,
        long totalElements,
        int totalPages
) {

    public static AdminAlertListResponse from(Page<AdminAlertResponse> page) {
        return new AdminAlertListResponse(
                page.getContent(),
                page.hasNext(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}


