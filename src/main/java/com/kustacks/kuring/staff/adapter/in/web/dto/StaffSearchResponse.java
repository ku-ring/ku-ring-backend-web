package com.kustacks.kuring.staff.adapter.in.web.dto;

import com.kustacks.kuring.staff.application.port.in.dto.StaffSearchResult;

public record StaffSearchResponse(
        String name,
        String major,
        String lab,
        String phone,
        String email,
        String deptName,
        String collegeName
) {

    public static StaffSearchResponse from(StaffSearchResult result) {
        return new StaffSearchResponse(
                result.name(),
                result.major(),
                result.lab(),
                result.phone(),
                result.email(),
                result.deptName(),
                result.collegeName()
        );
    }
}
