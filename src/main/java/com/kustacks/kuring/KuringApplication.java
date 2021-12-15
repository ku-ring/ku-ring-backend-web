package com.kustacks.kuring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@Slf4j
@EnableScheduling
@EnableRetry
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
}
