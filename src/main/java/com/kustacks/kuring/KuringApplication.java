package com.kustacks.kuring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@Slf4j
@SpringBootApplication
public class KuringApplication {

	public static void main(String[] args) {
		SpringApplication.run(KuringApplication.class, args);
	}

	@PostConstruct
	public void setTimezone() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
		log.info("KuringApplication TimeZone = {}", TimeZone.getDefault());
	}

	@Autowired
	ApplicationContext applicationContext;

	@Bean
	public ApplicationRunner applicationRunner() {
		return args -> {
			Environment environment = applicationContext.getEnvironment();
			String property = environment.getProperty("spring.datasource.hikari.maximum-pool-size");
			System.out.println("HikariPool connection size = " + property);
		};
	}
}
