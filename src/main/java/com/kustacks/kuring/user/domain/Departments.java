package com.kustacks.kuring.user.domain;

import com.kustacks.kuring.notice.domain.DepartmentName;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
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
