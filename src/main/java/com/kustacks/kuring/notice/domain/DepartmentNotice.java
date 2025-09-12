package com.kustacks.kuring.notice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DepartmentNotice extends Notice {

    @Column(name = "department_name")
    @Enumerated(EnumType.STRING)
    private DepartmentName departmentName;

    @Getter
    @Column(name = "graduated")
    private Boolean graduated;

    @Builder
    public DepartmentNotice(String articleId, String postedDate, String updatedDate,
                            String subject, CategoryName categoryName, Boolean important,
                            String fullUrl, DepartmentName departmentName, Boolean graduated) {
        super(articleId, postedDate, updatedDate, subject, categoryName, important, fullUrl);
        this.departmentName = departmentName;
        this.graduated = graduated;
    }

    public String getDepartmentName() {
        return departmentName.getName();
    }

    public String getDepartmentKorName() {
        return departmentName.getKorName();
    }

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
