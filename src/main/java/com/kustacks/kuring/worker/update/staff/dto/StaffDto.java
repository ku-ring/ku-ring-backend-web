package com.kustacks.kuring.worker.update.staff.dto;

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

    public boolean isNotSameInformation(Staff staff) {
        return !staff.isSameName(name)
                || !staff.isSameMajor(major)
                || !staff.isSameLab(lab)
                || !staff.isSamePhone(phone)
                || !staff.isSameEmail(email)
                || !staff.isSameDept(deptName)
                || !staff.isSameCollege(collegeName);
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

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StaffDto staffDto = (StaffDto) o;
        return Objects.equals(getName(), staffDto.getName())
                && Objects.equals(getMajor(), staffDto.getMajor())
                && Objects.equals(getLab(), staffDto.getLab())
                && Objects.equals(getPhone(), staffDto.getPhone())
                && Objects.equals(getEmail(), staffDto.getEmail())
                && Objects.equals(getDeptName(), staffDto.getDeptName())
                && Objects.equals(getCollegeName(), staffDto.getCollegeName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getMajor(), getLab(), getPhone(), getEmail(), getDeptName(), getCollegeName());
    }
}
