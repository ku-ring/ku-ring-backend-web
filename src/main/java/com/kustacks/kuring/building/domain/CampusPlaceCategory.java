package com.kustacks.kuring.building.domain;

import com.kustacks.kuring.common.exception.code.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Locale;

@Entity
@Getter
@Table(name = "campus_place_category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CampusPlaceCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false, unique = true)
    private String code;

    @Column(name = "kor_name", length = 50, nullable = false)
    private String korName;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Column(name = "filter_enabled", nullable = false)
    private boolean filterEnabled;

    public CampusPlaceCategory(String code, String korName, int displayOrder, boolean filterEnabled) {
        validate(code, korName, displayOrder);

        this.code = code.trim().toLowerCase(Locale.ROOT);
        this.korName = korName.trim();
        this.displayOrder = displayOrder;
        this.filterEnabled = filterEnabled;
    }

    private static void validate(String code, String korName, int displayOrder) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException(ErrorCode.CAMPUS_PLACE_CATEGORY_CODE_REQUIRED.getMessage());
        }
        if (korName == null || korName.isBlank()) {
            throw new IllegalArgumentException(ErrorCode.CAMPUS_PLACE_CATEGORY_KOREAN_NAME_REQUIRED.getMessage());
        }
        if (displayOrder <= 0) {
            throw new IllegalArgumentException(ErrorCode.CAMPUS_PLACE_CATEGORY_DISPLAY_ORDER_INVALID.getMessage());
        }
    }
}
