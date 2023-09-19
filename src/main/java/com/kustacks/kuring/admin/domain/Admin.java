package com.kustacks.kuring.admin.domain;

import com.kustacks.kuring.auth.userdetails.UserDetails;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "admin")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin implements UserDetails {

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

    @Override
    public String getPrincipal() {
        return this.loginId;
    }

    @Override
    public List<String> getAuthorities() {
        return this.adminRoles.getRoles()
                .stream()
                .map(AdminRole::name)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isValidCredentials(String credentials) {
        return this.password.equals(credentials);
    }
}


