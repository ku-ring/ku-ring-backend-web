package com.kustacks.kuring.feedback.domain;

import com.kustacks.kuring.user.domain.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "content", length = 256, nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "uid")
    private User user;

    public Feedback(String content, User user) {
        this.content = content;
        this.user = user;
    }
}
