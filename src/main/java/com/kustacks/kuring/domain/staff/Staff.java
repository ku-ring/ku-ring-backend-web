package com.kustacks.kuring.domain.staff;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "staff")
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "name", length = 10, nullable = false)
    private String name;

    @Column(name = "major", length = 32, nullable = false)
    private String major;

    @Column(name = "lab", length = 32, nullable = false)
    private String lab;

    @Column(name = "phone", length = 12, nullable = false)
    private String phone;

    @Column(name = "email", length = 40, nullable = false)
    private String email;

    @Builder
    public Staff(String name, String major, String lab, String phone, String email) {
        this.name = name;
        this.major = major;
        this.lab = lab;
        this.phone = phone;
        this.email = email;
    }
}
