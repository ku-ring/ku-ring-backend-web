package com.kustacks.kuring.club.domain;

import java.util.Arrays;
import java.util.List;

public enum ClubSnsType {
    INSTAGRAM(List.of("https://www.instagram.com", "https://instagram.com")),
    YOUTUBE(List.of("https://www.youtube.com", "https://youtube.com")),
    ETC(List.of());

    private List<String> urls;

    ClubSnsType(List<String> urls) {
        this.urls = urls;
    }

    public static ClubSnsType fromUrl(String url) {
        return Arrays.stream(ClubSnsType.values())
                .filter(
                        clubSnsType -> clubSnsType.urls.stream()
                                .anyMatch(url::startsWith)
                ).findFirst()
                .orElse(ETC);
    }
}
