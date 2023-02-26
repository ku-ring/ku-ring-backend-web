package com.kustacks.kuring.common.dto;

import com.kustacks.kuring.staff.domain.Staff;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StaffDto {

    private String name;

    private String major;

    private String lab;

    private String phone;

    private String email;

    private String deptName;

    private String collegeName;

    @Builder
    private StaffDto(String name, String major, String lab, String phone, String email, String deptName, String collegeName) {
        this.name = name;
        this.major = major;
        this.lab = lab;
        this.phone = phone;
        this.email = email;
        this.deptName = deptName;
        this.collegeName = collegeName;
    }

    public Staff toEntity() {
        return Staff.builder()
                .name(name)
                .major(major)
                .lab(lab)
                .phone(phone)
                .email(email)
                .dept(deptName)
                .college(collegeName).build();
    }

    public static StaffDto entityToDto(Staff staff) {
        return StaffDto.builder()
                .name(staff.getName())
                .major(staff.getMajor())
                .lab(staff.getLab())
                .phone(staff.getPhone())
                .email(staff.getEmail())
                .deptName(staff.getDept())
                .collegeName(staff.getCollege()).build();
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        StaffDto staffDTO = (StaffDto) o;
        return Objects.equals(staffDTO.getName(), name) && Objects.equals(staffDTO.getMajor(), major) && Objects.equals(staffDTO.getLab(), lab)
                && Objects.equals(staffDTO.getPhone(), phone) && Objects.equals(staffDTO.getEmail(), email) && Objects.equals(staffDTO.getDeptName(), deptName)
                && Objects.equals(staffDTO.getCollegeName(), collegeName);
    }
}
