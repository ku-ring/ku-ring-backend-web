package com.kustacks.kuring.persistence.category;

import com.kustacks.kuring.persistence.notice.Notice;
import com.kustacks.kuring.persistence.user_category.UserCategory;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "category")
public class Category {

    @Id
    @Column(name = "name", length = 45, nullable = false)
    private String name;

    @Column(name = "kor_name", nullable = false, unique = true, columnDefinition = "VARCHAR(45) default ''")
    private String korName = "";

    @Column(name = "short_name", nullable = false, unique = true, columnDefinition = "VARCHAR(15) default ''")
    private String shortName = "";

    @Column(name = "is_leaf", nullable = false)
    private boolean isLeaf;

    @ManyToOne(targetEntity = Category.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_name", referencedColumnName = "name", nullable = true)
    private Category parent;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    private List<Category> childs;

    // FetchMode.SELECT와 FetchType.LAZY로 설정해서
    // noticeList는 실제로 사용될 때 쿼리를 던지도록 함으로써 성능 향상을 꾀했다.
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SELECT)
    private List<Notice> noticeList = new ArrayList<>();

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SELECT)
    private List<UserCategory> userCategories = new ArrayList<>();

    @Builder
    public Category(boolean isLeaf, String name, String korName, String shortName, Category parent) {
        this.isLeaf = isLeaf;
        this.name = name;
        this.korName = korName;
        this.shortName = shortName;
        this.parent = parent;
    }
}
