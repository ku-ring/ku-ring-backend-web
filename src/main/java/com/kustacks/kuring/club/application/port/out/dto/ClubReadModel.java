package com.kustacks.kuring.club.application.port.out.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ClubReadModel {

    private final Long id;
    private final String name;
    private final String summary;
    private final String iconImageUrl;
    private final String category;
    private final String division;
    private final LocalDateTime recruitStartDate;
    private final LocalDateTime recruitEndDate;

    @QueryProjection
    public ClubReadModel(
            Long id,
            String name,
            String summary,
            String iconImageUrl,
            String category,
            String division,
            LocalDateTime recruitStartDate,
            LocalDateTime recruitEndDate
    ) {
        this.id = id;
        this.name = name;
        this.summary = summary;
        this.iconImageUrl = iconImageUrl;
        this.category = category;
        this.division = division;
        this.recruitStartDate = recruitStartDate;
        this.recruitEndDate = recruitEndDate;
    }
}
