package com.kustacks.kuring.club.domain;

import com.kustacks.kuring.common.exception.InvalidStateException;
import com.kustacks.kuring.common.exception.code.ErrorCode;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum ClubSnsType {
    INSTAGRAM(Set.of("instagram.com", "www.instagram.com", "m.instagram.com")),
    YOUTUBE(Set.of("youtube.com", "www.youtube.com", "m.youtube.com", "youtu.be")),
    ETC(Set.of());

    private final Set<String> hosts;

    private static final Map<String, ClubSnsType> HOST_TO_TYPE;

    static {
        Map<String, ClubSnsType> map = new HashMap<>();
        for (ClubSnsType type : ClubSnsType.values()) {
            for (String host : type.hosts) {
                map.put(host, type);
            }
        }
        HOST_TO_TYPE = Collections.unmodifiableMap(map);
    }

    ClubSnsType(Set<String> hosts) {
        this.hosts = hosts;
    }

    public static ClubSnsType fromUrl(String url) {
        String host = extractHost(url);
        return HOST_TO_TYPE.getOrDefault(host, ETC);
    }

    private static String extractHost(String url) {
        try {
            URI uri = URI.create(url.trim());
            return uri.getHost();
        } catch (IllegalArgumentException e) {
            throw new InvalidStateException(ErrorCode.API_INVALID_PARAM);
        }
    }
}
