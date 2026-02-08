package com.kustacks.kuring.club.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "club")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30, nullable = false)
    private String name;

    @Column(length = 30, nullable = false)
    private String summary;

    @Lob
    private String description;

    @Enumerated(EnumType.STRING)
    private ClubCategory category;

    @Enumerated(EnumType.STRING)
    private ClubDivision division;

    @Column(name = "instagram_url", length = 255)
    private String instagramUrl;

    @Column(name = "poster_image_path", length = 255)
    private String posterImagePath;

    @Column(length = 30)
    private String building;

    @Column(length = 30)
    private String room;

    private Double x;

    private Double y;

    @Column(name = "recruit_start_at")
    private LocalDateTime recruitStartAt;

    @Column(name = "recruit_end_at")
    private LocalDateTime recruitEndAt;

    @Column(name = "is_always", nullable = false)
    private boolean isAlways = false;

    @Column(name = "apply_url", length = 255)
    private String applyUrl;

    @Lob
    private String qualifications;
}
