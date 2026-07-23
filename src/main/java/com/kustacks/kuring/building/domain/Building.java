package com.kustacks.kuring.building.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Table(name = "building")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Building {

    private static final String DEFAULT_ADDRESS = "서울특별시 광진구 능동로 120";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30, nullable = false)
    private String name;

    @Column(length = 100, nullable = false)
    private String address;

    private Double lat;

    private Double lon;

    @Column(name = "image_path")
    private String imagePath;

    @OneToMany(
            mappedBy = "building",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<BuildingSearchKeyword> keywords = new ArrayList<>();

    @OneToMany(
            mappedBy = "building",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("displayOrder ASC, id ASC")
    private List<CampusPlace> campusPlaces = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "building_operating_hours",
            joinColumns = @JoinColumn(name = "building_id")
    )
    private Set<OperatingHours> operatingHours = new HashSet<>();

    public Building(String name, Double lat, Double lon) {
        this(name, DEFAULT_ADDRESS, lat, lon, null);
    }

    public Building(String name, String address, Double lat, Double lon, String imagePath) {
        validateRequiredFields(name, address);

        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
        this.imagePath = imagePath;
    }

    public void addSearchKeyword(String keyword) {
        keywords.add(new BuildingSearchKeyword(this, keyword));
    }

    public void addCampusPlace(CampusPlace campusPlace) {
        if (campusPlace == null) {
            throw new IllegalArgumentException("캠퍼스 시설은 필수입니다.");
        }
        campusPlaces.add(campusPlace);
    }

    public void addOperatingHours(OperatingHours hours) {
        validateOperatingHours(hours);
        operatingHours.add(hours);
    }

    private void validateOperatingHours(OperatingHours hours) {
        if (hours == null) {
            throw new IllegalArgumentException("운영시간은 필수입니다.");
        }

        boolean alreadyExists = operatingHours.stream()
                .anyMatch(existing -> existing.matches(
                        hours.getPeriod(),
                        hours.getDayGroup()
                ));

        if (alreadyExists) {
            throw new IllegalArgumentException(
                    "같은 기간과 요일의 운영시간은 중복될 수 없습니다."
            );
        }
    }

    private static void validateRequiredFields(String name, String address) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("건물명은 필수입니다.");
        }
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("건물 주소는 필수입니다.");
        }
    }
}
