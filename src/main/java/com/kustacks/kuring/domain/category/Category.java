package com.kustacks.kuring.domain.category;

import com.kustacks.kuring.domain.notice.Notice;
import com.kustacks.kuring.domain.user_category.UserCategory;
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

    // FetchMode.SELECT와 FetchType.LAZY로 설정해서
    // noticeList는 실제로 사용될 때 쿼리를 던지도록 함으로써 성능 향상을 꾀했다.
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SELECT)
    private List<Notice> noticeList = new ArrayList<>();

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SELECT)
    private List<UserCategory> userCategories = new ArrayList<>();

    @Builder
    public Category(String name) {
        this.name = name;
    }
}
