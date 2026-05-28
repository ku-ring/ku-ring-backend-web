package com.kustacks.kuring.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        // TODO: KUIS/Library/Auth HTTP 클라이언트를 RestClient로 전환한 뒤 제거한다.
        return restTemplateBuilder.build();
    }
}
