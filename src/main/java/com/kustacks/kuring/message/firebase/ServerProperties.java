package com.kustacks.kuring.message.firebase;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "server.deploy")
public class ServerProperties {

    private final String environment;

    public boolean isSameEnvironment(String devSuffix) {
        return this.environment.equals(devSuffix);
    }
}
