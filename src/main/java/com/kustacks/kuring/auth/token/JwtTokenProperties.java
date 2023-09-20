package com.kustacks.kuring.auth.token;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "security.jwt.token")
public class JwtTokenProperties {

    private String secretKey;
    private long expireLength;

    public JwtTokenProperties(String secretKey, String expireLength) {
        this.secretKey = secretKey;
        this.expireLength = Long.parseLong(expireLength);
    }
}
