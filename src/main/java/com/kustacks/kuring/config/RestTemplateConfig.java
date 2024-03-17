package com.kustacks.kuring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
//        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
//        factory.setConnectTimeout(5000);  //5초
//        factory.setReadTimeout(5000);     //5초
//        CloseableHttpClient httpClient = HttpClientBuilder.create()
//                .setMaxConnTotal(100)
//                .setMaxConnPerRoute(5)
//                .build();
//        factory.setHttpClient(httpClient);
//        return new RestTemplate(factory);
    }
}
