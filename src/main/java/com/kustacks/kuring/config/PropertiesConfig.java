package com.kustacks.kuring.config;

import com.kustacks.kuring.worker.client.notice.LatestPageProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({LatestPageProperties.class})
public class PropertiesConfig {
}
