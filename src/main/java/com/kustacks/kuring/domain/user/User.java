package com.kustacks.kuring.domain.user;

import com.kustacks.kuring.domain.feedback.Feedback;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "token", unique = true, length = 64, nullable = false)
    private String token;

    @OneToMany(mappedBy = "user")
    private List<Feedback> feedbacks;

    @Builder
    public User(String token) {
        this.token = token;
        feedbacks = new ArrayList<>();
    }
}
