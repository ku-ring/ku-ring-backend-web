package com.kustacks.kuring.auth.token;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt.token")
public record JwtTokenProperties(
        String secretKey,
        long adminExpireLength,
        long userExpireLength
) {
}
