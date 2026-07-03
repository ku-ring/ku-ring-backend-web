package com.kustacks.kuring.club.domain;

import com.kustacks.kuring.building.domain.Building;
import jakarta.persistence.Column;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(length = 255, nullable = false)
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClubCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClubDivision division;

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClubSns> homepageUrls = new ArrayList<>();

    @Column(name = "poster_image_path", length = 255)
    private String posterImagePath;

    @Column(name = "icon_image_path", length = 255)
    private String iconImagePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id")
    private Building building;

    @Column(length = 30)
    private String room;

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

    public Club(
            String name,
            String summary,
            String description,
            ClubCategory category,
            ClubDivision division,
            Building building,
            String room,
            LocalDateTime recruitStartAt,
            LocalDateTime recruitEndAt,
            boolean isAlways,
            String applyUrl,
            String qualifications,
            String iconImagePath,
            String posterImagePath
    ) {
        this.name = name;
        this.summary = summary;
        this.description = description;
        this.category = category;
        this.division = division;
        this.building = building;
        this.room = room;
        this.recruitStartAt = recruitStartAt;
        this.recruitEndAt = recruitEndAt;
        this.isAlways = isAlways;
        this.applyUrl = applyUrl;
        this.qualifications = qualifications;
        this.iconImagePath = iconImagePath;
        this.posterImagePath = posterImagePath;
    }
}
