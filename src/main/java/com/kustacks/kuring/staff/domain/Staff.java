package com.kustacks.kuring.staff.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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
    private Staff(String name, String major, String lab, String phone, String email, String dept, String college) {
        this.name = new Name(name);
        this.major = major;
        this.lab = lab;
        this.phone = new Phone(phone);
        this.email = new Email(email);
        this.dept = dept;
        this.college = College.valueOf(college);
    }

    public void changeInformation(String name, String major, String lab, String phone, String email, String deptName, String college) {
        this.name = new Name(name);
        this.major = major;
        this.lab = lab;
        this.phone = new Phone(phone);
        this.email = new Email(email);
        this.dept = deptName;
        this.college = College.valueOf(college);
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
