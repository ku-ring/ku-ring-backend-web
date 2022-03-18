package com.kustacks.kuring.controller.dto;

import com.kustacks.kuring.persistence.staff.Staff;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter @Setter
@AllArgsConstructor
@Builder
public class StaffDTO {

    private String name;

    private String major;

    private String lab;

    private String phone;

    private String email;

    private String deptName;

    private String collegeName;

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        StaffDTO staffDTO = (StaffDTO) o;
        return Objects.equals(staffDTO.getName(), name) && Objects.equals(staffDTO.getMajor(), major) && Objects.equals(staffDTO.getLab(), lab)
                && Objects.equals(staffDTO.getPhone(), phone) && Objects.equals(staffDTO.getEmail(), email) && Objects.equals(staffDTO.getDeptName(), deptName)
                && Objects.equals(staffDTO.getCollegeName(), collegeName);
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

    public static StaffDTO entityToDTO(Staff staff) {
        return StaffDTO.builder()
                .name(staff.getName())
                .major(staff.getMajor())
                .lab(staff.getLab())
                .phone(staff.getPhone())
                .email(staff.getEmail())
                .deptName(staff.getDept())
                .collegeName(staff.getCollege()).build();
    }
}
