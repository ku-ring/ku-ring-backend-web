package com.kustacks.kuring.user.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Content {

    private static final int MAX_LENGTH = 256;

    @Column(name = "content", length = 256, nullable = false)
    private String value;

    public Content(String content) {
        validate(content);
        this.value = content;
    }

    public String getValue() {
        return value;
    }

    private void validate(String content) {
        if (content.length() > MAX_LENGTH) throw new IllegalArgumentException("본문 내용은 " + MAX_LENGTH + "자 이하여야 합니다");
        if (content.isBlank()) throw new IllegalArgumentException("본문은 공백일 수 없습니다");
    }
}
