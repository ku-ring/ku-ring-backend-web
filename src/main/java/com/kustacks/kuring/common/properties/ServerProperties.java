package com.kustacks.kuring.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "server.deploy")
public record ServerProperties(
        String environment
) {
    private static final String DEV_SUFFIX = "dev";

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
