package com.kustacks.kuring.club.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClubCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClubDivision division;

    @Column(name = "poster_image_path", length = 255)
    private String posterImagePath;

    @Column(name = "icon_image_path", length = 255)
    private String iconImagePath;

    @Column(length = 30)
    private String building;

    @Column(length = 30)
    private String room;

    private Double lat;

    private Double lon;

    @Column(name = "recruit_start_at")
    private LocalDateTime recruitStartAt;

    @Column(name = "recruit_end_at")
    private LocalDateTime recruitEndAt;

    @Column(name = "is_always", nullable = false)
    private boolean isAlways = false;

    @Column(name = "apply_url", length = 255)
    private String applyUrl;

    @Column(columnDefinition = "TEXT")
    private String qualifications;

    public Club(String name, String summary, String description,
            ClubCategory category, ClubDivision division,
            String building, String room, Double lat, Double lon,
            LocalDateTime recruitStartAt, LocalDateTime recruitEndAt, boolean isAlways, String applyUrl, String qualifications,
            String iconImagePath, String posterImagePath
    ) {
        this.name = name;
        this.summary = summary;
        this.description = description;

        this.category = category;
        this.division = division;

        this.building = building;
        this.room = room;
        this.lat = lat;
        this.lon = lon;

        this.recruitStartAt = recruitStartAt;
        this.recruitEndAt = recruitEndAt;
        this.isAlways = isAlways;
        this.applyUrl = applyUrl;
        this.qualifications = qualifications;

        this.iconImagePath = iconImagePath;
        this.posterImagePath = posterImagePath;
    }
}
