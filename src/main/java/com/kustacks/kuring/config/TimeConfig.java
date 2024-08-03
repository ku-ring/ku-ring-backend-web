package com.kustacks.kuring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class TimeConfig {

    private static final String ASIA_SEOUL_ZONE = "Asia/Seoul";

    @Bean
    public Clock clock() {
        ZoneId seoulZoneId = ZoneId.of(ASIA_SEOUL_ZONE);
        return Clock.system(seoulZoneId);
    }
}
