package com.kustacks.kuring.notice.domain;

import com.kustacks.kuring.category.domain.Category;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DepartmentNotice extends Notice {

    @Column(name = "department_name")
    @Enumerated(EnumType.STRING)
    private DepartmentName departmentName;

    @Builder
    public DepartmentNotice(String articleId, String postedDate, String updatedDate, String subject, Category category, Boolean important, String fullUrl, DepartmentName departmentName) {
        super(articleId, postedDate, updatedDate, subject, category, important, fullUrl);
        this.departmentName = departmentName;
    }

    public String getDepartmentName() {
        return departmentName.getName();
    }

    public String getDepartmentKorName() { return departmentName.getKorName(); }
}
