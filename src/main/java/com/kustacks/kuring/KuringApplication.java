package com.kustacks.kuring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class KuringApplication {

	public static void main(String[] args) {
		SpringApplication.run(KuringApplication.class, args);
	}
}
