package com.kustacks.kuring.user.domain;

import com.kustacks.kuring.category.domain.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "user_category")
public class UserCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_token", referencedColumnName = "token", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_name", nullable = false)
    private Category category;

    public UserCategory(User user, Category category) {
        this.user = user;
        this.category = category;
    }

    public String getCategoryName() {
        return this.category.getName();
    }
}
