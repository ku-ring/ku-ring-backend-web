package com.kustacks.kuring.user.domain;

import com.kustacks.kuring.category.domain.CategoryName;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Categories implements Serializable {

    @ElementCollection
    @CollectionTable(
            name = "user_categories",
            joinColumns = @JoinColumn(name = "id")
    )
    @Column(name = "category_name")
    @Enumerated(EnumType.STRING)
    private Set<CategoryName> categoryNamesSet = new HashSet<>();

    public void add(CategoryName categoryName) {
        this.categoryNamesSet.add(categoryName);
    }

    public void delete(CategoryName categoryName) {
        this.categoryNamesSet.remove(categoryName);
    }

    public Set<CategoryName> getCategoryNamesSet() {
        return Collections.unmodifiableSet(categoryNamesSet);
    }

    public boolean contains(CategoryName categoryName) {
        return this.categoryNamesSet.contains(categoryName);
    }
}
