package com.kustacks.kuring.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Device implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(unique = true, nullable = false)
    private String fcmToken;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @Getter(AccessLevel.PUBLIC)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_user_id")
    @Getter(AccessLevel.PUBLIC)
    private User originUser;

    public Device(String fcmToken, User user) {
        this.fcmToken = fcmToken;
        this.user = user;
        this.originUser = user;
    }

    public void login(User user) {
        this.user = user;

    }

    public void logout() {
        this.user = this.originUser;
    }
}
