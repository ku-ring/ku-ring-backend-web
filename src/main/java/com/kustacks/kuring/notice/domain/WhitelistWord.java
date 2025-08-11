package com.kustacks.kuring.notice.domain;

import com.kustacks.kuring.common.domain.Activatable;
import com.kustacks.kuring.common.domain.BaseTimeEntity;
import com.kustacks.kuring.common.domain.WordHolder;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WhitelistWord extends BaseTimeEntity implements WordHolder, Activatable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "word", nullable = false, length = 128)
    private String word;

    @Column(name = "description", length = 256)
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    public WhitelistWord(String word, String description, Boolean isActive) {
        this.word = word;
        this.description = description;
        this.isActive = isActive != null ? isActive : true;
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }
}
