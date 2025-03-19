package com.kustacks.kuring.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.io.Serializable;

import static com.kustacks.kuring.user.domain.User.FCM_USER_MONTHLY_QUESTION_COUNT;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "update root_user set deleted = true where id = ?")
@SQLRestriction("deleted = false")
public class RootUser implements Serializable {
    public static final int ROOT_USER_EXTRA_QUESTION_COUNT = 3;
    public static final int ROOT_USER_MONTHLY_QUESTION_COUNT = FCM_USER_MONTHLY_QUESTION_COUNT + ROOT_USER_EXTRA_QUESTION_COUNT;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter(AccessLevel.PUBLIC)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Getter(AccessLevel.PUBLIC)
    @Column(unique = true, length = 256)
    private String email;

    @Getter(AccessLevel.PUBLIC)
    @Column(nullable = true, length = 256)
    private String password;

    @Column(unique = true, length = 256)
    private String nickname;

    @Getter(AccessLevel.PUBLIC)
    @Column(columnDefinition = "integer default 0")
    private Integer questionCount;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = Boolean.FALSE;

    public RootUser(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.questionCount = ROOT_USER_MONTHLY_QUESTION_COUNT;
    }

    public void updateQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    public void reactive(String password) {
        this.password = password;
        this.deleted = Boolean.FALSE;
        this.questionCount = ROOT_USER_MONTHLY_QUESTION_COUNT;
    }
}
