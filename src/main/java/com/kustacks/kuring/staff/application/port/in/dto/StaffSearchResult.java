package com.kustacks.kuring.staff.application.port.in.dto;

public record StaffSearchResult(
        String name,
        String major,
        String lab,
        String phone,
        String email,
        String deptName,
        String collegeName
) {
}
