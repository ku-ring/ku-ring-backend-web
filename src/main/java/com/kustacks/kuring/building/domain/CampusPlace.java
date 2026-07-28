package com.kustacks.kuring.building.domain;

import com.kustacks.kuring.common.exception.code.ErrorCode;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Table(name = "campus_place")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CampusPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CampusPlaceCategory category;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(name = "image_path")
    private String imagePath;

    @Enumerated(EnumType.STRING)
    @Column(name = "location_type", length = 20, nullable = false)
    private CampusPlaceLocationType locationType;

    @Column(length = 20)
    private String floor;

    @Column(name = "location_detail")
    private String locationDetail;

    private Integer quantity;

    @Column(name = "external_url", length = 500)
    private String externalUrl;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "campus_place_operating_hours",
            joinColumns = @JoinColumn(name = "campus_place_id")
    )
    private Set<OperatingHours> operatingHours = new HashSet<>();

    public CampusPlace(
            Building building,
            CampusPlaceCategory category,
            String name,
            String imagePath,
            CampusPlaceLocationType locationType,
            String floor,
            String locationDetail,
            Integer quantity,
            String externalUrl,
            int displayOrder
    ) {
        validateRequiredFields(building, category, name, locationType);
        validateQuantity(quantity);

        this.building = building;
        this.category = category;
        this.name = name;
        this.imagePath = imagePath;
        this.locationType = locationType;
        this.floor = floor;
        this.locationDetail = locationDetail;
        this.quantity = quantity;
        this.externalUrl = externalUrl;
        this.displayOrder = displayOrder;
    }

    public void addOperatingHours(OperatingHours hours) {
        validateOperatingHours(hours);
        operatingHours.add(hours);
    }

    private static void validateRequiredFields(
            Building building,
            CampusPlaceCategory category,
            String name,
            CampusPlaceLocationType locationType
    ) {
        if (building == null) {
            throw new IllegalArgumentException(ErrorCode.BUILDING_REQUIRED.getMessage());
        }
        if (category == null) {
            throw new IllegalArgumentException(ErrorCode.CAMPUS_PLACE_CATEGORY_REQUIRED.getMessage());
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(ErrorCode.CAMPUS_PLACE_NAME_REQUIRED.getMessage());
        }
        if (locationType == null) {
            throw new IllegalArgumentException(ErrorCode.CAMPUS_PLACE_LOCATION_TYPE_REQUIRED.getMessage());
        }
    }

    private static void validateQuantity(Integer quantity) {
        if (quantity != null && quantity <= 0) {
            throw new IllegalArgumentException(ErrorCode.CAMPUS_PLACE_QUANTITY_INVALID.getMessage());
        }
    }

    private void validateOperatingHours(OperatingHours hours) {
        if (hours == null) {
            throw new IllegalArgumentException(ErrorCode.OPERATING_HOURS_REQUIRED.getMessage());
        }

        boolean alreadyExists = operatingHours.stream()
                .anyMatch(existing -> existing.matches(
                        hours.getPeriod(),
                        hours.getDayGroup()
                ));

        if (alreadyExists) {
            throw new IllegalArgumentException(ErrorCode.OPERATING_HOURS_DUPLICATED.getMessage());
        }
    }
}
