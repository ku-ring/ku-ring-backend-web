package com.kustacks.kuring.admin.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminRoles implements Serializable {

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "admin_roles",
            joinColumns = @JoinColumn(name = "id"))
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Set<AdminRole> roles = new HashSet<>();

    public void add(AdminRole role) {
        this.roles.add(role);
    }

    public void delete(AdminRole role) {
        this.roles.remove(role);
    }

    public List<AdminRole> getRoles() {
        return List.copyOf(roles);
    }
}
