package com.kustacks.kuring.notice.domain;

import com.kustacks.kuring.common.domain.Activatable;
import com.kustacks.kuring.common.domain.BaseTimeEntity;
import com.kustacks.kuring.common.domain.WordHolder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BadWord extends BaseTimeEntity implements WordHolder, Activatable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "word", nullable = false, length = 128)
    private String word;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 32)
    private BadWordCategory category;

    @Column(name = "description", length = 256)
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    public BadWord(String word, BadWordCategory category, String description, Boolean isActive) {
        this.word = word;
        this.category = category;
        this.description = description;
        this.isActive = isActive != null ? isActive : true;
    }

    @Override
    public void activate() {
        this.isActive = true;
    }

    @Override
    public void deactivate() {
        this.isActive = false;
    }

    public void updateWord(String word) {
        this.word = word;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateCategory(BadWordCategory category) {
        this.category = category;
    }
}
