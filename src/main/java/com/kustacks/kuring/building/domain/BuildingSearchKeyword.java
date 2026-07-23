package com.kustacks.kuring.building.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Getter
@Table(name = "building_search_keyword")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BuildingSearchKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    @Column(length = 50, nullable = false)
    private String keyword;

    BuildingSearchKeyword(Building building, String keyword) {
        if (building == null) {
            throw new IllegalArgumentException("건물은 필수입니다.");
        }
        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("건물 검색어는 필수입니다.");
        }

        this.building = building;
        this.keyword = keyword.trim();
    }
}
