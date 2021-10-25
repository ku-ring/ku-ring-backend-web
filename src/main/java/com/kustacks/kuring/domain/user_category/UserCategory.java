package com.kustacks.kuring.domain.user_category;

import com.kustacks.kuring.domain.category.Category;
import com.kustacks.kuring.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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

    @Builder
    public UserCategory(User user, Category category) {
        this.user = user;
        this.category = category;
    }
}
