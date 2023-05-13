package com.kustacks.kuring.staff.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Staff {

    @Id
    @Getter(AccessLevel.PRIVATE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Getter(AccessLevel.PUBLIC)
    @Column(name = "name", length = 64, nullable = false)
    private String name;

    @Getter(AccessLevel.PUBLIC)
    @Column(name = "major", length = 128, nullable = false)
    private String major;

    @Getter(AccessLevel.PUBLIC)
    @Column(name = "lab", length = 64)
    private String lab;

    @Getter(AccessLevel.PUBLIC)
    @Column(name = "phone", length = 64)
    private String phone;

    @Getter(AccessLevel.PUBLIC)
    @Column(name = "email", length = 40, nullable = false)
    private String email;

    @Getter(AccessLevel.PUBLIC)
    @Column(name = "dept", length = 128, nullable = false)
    private String dept;

    @Getter(AccessLevel.PUBLIC)
    @Column(name = "college", length = 64, nullable = false)
    private String college;

    @Builder
    public Staff(String name, String major, String lab, String phone, String email, String dept, String college) {
        this.name = name;
        this.major = major;
        this.lab = lab;
        this.phone = phone;
        this.email = email;
        this.dept = dept;
        this.college = college;
    }

    public void changeInformation(String name, String major, String lab, String phone, String email, String deptName, String collegeName) {
        this.name = name;
        this.major = major;
        this.lab = lab;
        this.phone = phone;
        this.email = email;
        this.dept = deptName;
        this.college = collegeName;
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
