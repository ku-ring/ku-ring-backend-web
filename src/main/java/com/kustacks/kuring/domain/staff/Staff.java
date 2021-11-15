package com.kustacks.kuring.domain.staff;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter @Setter
@NoArgsConstructor
@Entity
@Table(name = "staff")
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "name", length = 64, nullable = false)
    private String name;

    @Column(name = "major", length = 128, nullable = false)
    private String major;

    @Column(name = "lab", length = 64, nullable = true)
    private String lab;

    @Column(name = "phone", length = 64, nullable = true)
    private String phone;

    @Column(name = "email", length = 40, nullable = false)
    private String email;

    @Column(name = "dept", length = 128, nullable = false)
    private String dept;

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
}
