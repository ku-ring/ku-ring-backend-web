package com.kustacks.kuring.notice.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Objects;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DepartmentNotice extends Notice {

    @Column(name = "department_name")
    @Enumerated(EnumType.STRING)
    private DepartmentName departmentName;

    @Builder
    public DepartmentNotice(String articleId, String postedDate, String updatedDate, String subject, CategoryName categoryName, Boolean important, String fullUrl, DepartmentName departmentName) {
        super(articleId, postedDate, updatedDate, subject, categoryName, important, fullUrl);
        this.departmentName = departmentName;
    }

    public String getDepartmentName() {
        return departmentName.getName();
    }

    public String getDepartmentKorName() { return departmentName.getKorName(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DepartmentNotice notice = (DepartmentNotice) o;
        return Objects.equals(getId(), notice.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
