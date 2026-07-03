package com.kustacks.kuring.club.domain;

import com.kustacks.kuring.notice.domain.Url;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "club_sns")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClubSns {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClubSnsType type;

    @Embedded
    private Url url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    public ClubSns(ClubSnsType type, String url, Club club) {
        this.type = type;
        this.url = new Url(url);
        this.club = club;
    }
}
