package com.kustacks.kuring.user.domain;

import com.kustacks.kuring.notice.domain.DepartmentName;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Departments implements Serializable {

    @ElementCollection
    @CollectionTable(
            name = "user_departments",
            joinColumns = @JoinColumn(name = "id")
    )
    @Column(name = "department_name")
    @Enumerated(EnumType.STRING)
    private Set<DepartmentName> departmentNamesSet = new HashSet<>();

    public void add(DepartmentName departmentName) {
        this.departmentNamesSet.add(departmentName);
    }

    public void delete(DepartmentName departmentName) {
        this.departmentNamesSet.remove(departmentName);
    }

    public Set<DepartmentName> getDepartmentNamesSet() {
        return Collections.unmodifiableSet(departmentNamesSet);
    }

    public boolean contains(DepartmentName departmentName) {
        return this.departmentNamesSet.contains(departmentName);
    }
}
