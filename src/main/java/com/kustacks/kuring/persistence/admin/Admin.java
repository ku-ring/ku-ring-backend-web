package com.kustacks.kuring.persistence.admin;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "admin")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "token", unique = true, nullable = false)
    private String token;

    @Column(name = "owner", unique = true, nullable = false)
    private String owner;
}
