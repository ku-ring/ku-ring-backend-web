package com.kustacks.kuring.staff.common.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StaffSearchDto {

    private String name;

    private String major;

    private String lab;

    private String phone;

    private String email;

    private String deptName;

    private String collegeName;

    @QueryProjection
    public StaffSearchDto(String name, String major, String lab, String phone, String email, String deptName, String collegeName) {
        this.name = name;
        this.major = major;
        this.lab = lab;
        this.phone = phone;
        this.email = email;
        this.deptName = deptName;
        this.collegeName = collegeName;
    }
}
