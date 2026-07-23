package com.kustacks.kuring.building.domain;

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
            throw new IllegalArgumentException("캠퍼스 시설 카테고리 코드는 필수입니다.");
        }
        if (korName == null || korName.isBlank()) {
            throw new IllegalArgumentException("캠퍼스 시설 카테고리 한글명은 필수입니다.");
        }
        if (displayOrder <= 0) {
            throw new IllegalArgumentException("카테고리 노출 순서는 양수여야 합니다.");
        }
    }
}
