package com.kustacks.kuring.category.domain;

import com.kustacks.kuring.worker.CategoryName;
import com.kustacks.kuring.notice.domain.Notice;
import com.kustacks.kuring.user.domain.UserCategory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Category {

    @Id
    @Column(name = "name", length = 20, nullable = false)
    private String name;

    @OneToMany(mappedBy = "category")
    private List<Notice> noticeList = new ArrayList<>();

    @OneToMany(mappedBy = "category")
    private List<UserCategory> userCategories = new ArrayList<>();

    public Category(String name) {
        this.name = name;
    }

    public boolean isSameName(CategoryName categoryName) {
        return categoryName.isSameName(this.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(getName(), category.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
