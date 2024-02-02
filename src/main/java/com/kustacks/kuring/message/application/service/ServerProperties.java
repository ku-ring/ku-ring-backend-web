package com.kustacks.kuring.message.application.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "server.deploy")
public class ServerProperties {

    private static final String DEV_SUFFIX = "dev";

    private final String environment;

    public boolean isSameEnvironment(String devSuffix) {
        return this.environment.equals(devSuffix);
    }

    public String ifDevThenAddSuffix(String topic) {
        StringBuilder topicBuilder = new StringBuilder(topic);
        if (this.isSameEnvironment(DEV_SUFFIX)) {
            topicBuilder.append(".").append(DEV_SUFFIX);
        }

        return topicBuilder.toString();
    }

    public String addDevSuffix(String topic) {
        return new StringBuilder(topic)
                .append(".").append(DEV_SUFFIX)
                .toString();
    }
}
