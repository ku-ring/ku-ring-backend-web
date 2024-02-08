package com.kustacks.kuring.staff.adapter.in.web.dto;

import java.util.List;

public record StaffSearchListResponse(
        List<StaffSearchResponse> staffList
) {
}
