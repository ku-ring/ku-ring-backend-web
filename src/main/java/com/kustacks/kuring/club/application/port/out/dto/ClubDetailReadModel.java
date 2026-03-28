package com.kustacks.kuring.club.application.port.out.dto;

import com.kustacks.kuring.club.domain.ClubCategory;
import com.kustacks.kuring.club.domain.ClubDivision;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClubDetailReadModel {

    private Long id;
    private String name;
    private String summary;
    private ClubCategory category;
    private ClubDivision division;

    private String instagramUrl;
    private String youtubeUrl;
    private String etcUrl;

    private String description;
    private String qualifications;

    private LocalDateTime recruitStartAt;
    private LocalDateTime recruitEndAt;
    Boolean isAlways;

    private String applyUrl;
    private String posterImagePath;

    private String building;
    private String room;
    private Double lon;
    private Double lat;

    @QueryProjection
    public ClubDetailReadModel(
            Long id,
            String name,
            String summary,
            ClubCategory category,
            ClubDivision division,
            String instagramUrl,
            String youtubeUrl,
            String etcUrl,
            String description,
            String qualifications,
            LocalDateTime recruitStartAt,
            LocalDateTime recruitEndAt,
            Boolean isAlways,
            String applyUrl,
            String posterImagePath,
            String building,
            String room,
            Double lon,
            Double lat
    ) {
        this.id = id;
        this.name = name;
        this.summary = summary;
        this.category = category;
        this.division = division;
        this.instagramUrl = instagramUrl;
        this.youtubeUrl = youtubeUrl;
        this.etcUrl = etcUrl;
        this.description = description;
        this.qualifications = qualifications;
        this.recruitStartAt = recruitStartAt;
        this.recruitEndAt = recruitEndAt;
        this.isAlways = isAlways;
        this.applyUrl = applyUrl;
        this.posterImagePath = posterImagePath;
        this.building = building;
        this.room = room;
        this.lon = lon;
        this.lat = lat;
    }

    public boolean hasLocation() {
        return building != null
                || room != null
                || lon != null
                || lat != null;
    }
}