package com.kustacks.kuring.admin.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "admin")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin {

    @Id
    @Getter(AccessLevel.PROTECTED)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "admin_login_id", nullable = false)
    private String loginId;

    @Column(name = "admin_password", nullable = false)
    private String password;

    @Embedded
    private AdminRoles adminRoles = new AdminRoles();

    public Admin(String loginId, String password) {
        this.loginId = loginId;
        this.password = password;
    }

    public void addRole(AdminRole adminRole) {
        this.adminRoles.add(adminRole);
    }

    public List<AdminRole> getRoles() {
        return this.adminRoles.getRoles();
    }
}


