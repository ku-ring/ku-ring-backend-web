package com.kustacks.kuring.auth.token;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "security.jwt.token")
public class JwtTokenProperties {

    private String secretKey;
    private long expireLength;
}
