package com.kustacks.kuring.staff.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Staff {

    @Id
    @Getter(AccessLevel.PRIVATE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Embedded
    private Name name;

    @Getter(AccessLevel.PUBLIC)
    @Column(name = "major", length = 128, nullable = false)
    private String major;

    @Getter(AccessLevel.PUBLIC)
    @Column(name = "lab", length = 64)
    private String lab;

    @Getter(AccessLevel.PUBLIC)
    @Column(name = "position", length = 64)
    private String position;

    @Embedded
    private Phone phone;

    @Embedded
    private Email email;

    @Getter(AccessLevel.PUBLIC)
    @Column(name = "dept", length = 128, nullable = false)
    private String dept;

    @Getter(AccessLevel.PUBLIC)
    @Enumerated(EnumType.STRING)
    @Column(name = "college", length = 64, nullable = false)
    private College college;

    @Builder
    private Staff(String name, String major, String lab, String phone, String email, String dept, String college, String position) {
        this.name = new Name(name);
        this.major = major;
        this.lab = lab;
        this.phone = new Phone(phone);
        this.email = new Email(email);
        this.dept = dept;
        this.college = College.valueOf(college);
        this.position = position;
    }

    public void updateInformation(String name, String major, String lab, String phone, String email, String deptName, String college, String position) {
        this.name = new Name(name);
        this.major = major;
        this.lab = lab;
        this.phone = new Phone(phone);
        this.email = new Email(email);
        this.dept = deptName;
        this.college = College.valueOf(college);
        this.position = position;
    }

    public String getEmail() {
        return this.email.getValue();
    }

    public String getPhone() {
        return this.phone.getValue();
    }

    public String getName() {
        return this.name.getValue();
    }

    public boolean isSameName(String name) {
        return this.name.isSameValue(name);
    }

    public boolean isSameMajor(String major) {
        return this.major.equals(major);
    }

    public boolean isSameLab(String lab) {
        return this.lab.equals(lab);
    }

    public boolean isSamePhone(String phone) {
        return this.phone.isSameValue(phone);
    }

    public boolean isSameEmail(String email) {
        return this.email.isSameValue(email);
    }

    public boolean isSameDept(String deptName) {
        return this.dept.equals(deptName);
    }

    public boolean isSameCollege(String collegeName) {
        return this.college == College.valueOf(collegeName);
    }

    public boolean isSamePosition(String position) {
        return this.position.equals(position);
    }

    public String identifier() {
        return String.join(",", getName(), position, dept);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Staff staff = (Staff) o;
        return Objects.equals(getId(), staff.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
