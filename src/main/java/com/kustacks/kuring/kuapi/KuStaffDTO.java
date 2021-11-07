package com.kustacks.kuring.kuapi;

import com.kustacks.kuring.domain.staff.Staff;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter @Setter
@AllArgsConstructor
@Builder
public class KuStaffDTO {

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

        KuStaffDTO kuStaffDTO = (KuStaffDTO) o;
        return Objects.equals(kuStaffDTO.getName(), name) && Objects.equals(kuStaffDTO.getMajor(), major) && Objects.equals(kuStaffDTO.getLab(), lab)
                && Objects.equals(kuStaffDTO.getPhone(), phone) && Objects.equals(kuStaffDTO.getEmail(), email) && Objects.equals(kuStaffDTO.getDeptName(), deptName)
                && Objects.equals(kuStaffDTO.getCollegeName(), collegeName);
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

    public static KuStaffDTO entityToDTO(Staff staff) {
        return KuStaffDTO.builder()
                .name(staff.getName())
                .major(staff.getMajor())
                .lab(staff.getLab())
                .phone(staff.getPhone())
                .email(staff.getEmail())
                .deptName(staff.getDept())
                .collegeName(staff.getCollege()).build();
    }
}
