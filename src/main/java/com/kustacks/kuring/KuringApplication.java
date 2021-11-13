package com.kustacks.kuring;

import com.kustacks.kuring.kuapi.KuApiWatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.TimeZone;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class KuringApplication {

	private final KuApiWatcher kuApiWatcher;

	public KuringApplication(KuApiWatcher kuApiWatcher) {
		this.kuApiWatcher = kuApiWatcher;
	}

	public static void main(String[] args) throws IOException {
		SpringApplication.run(KuringApplication.class, args);
	}

	@PostConstruct
	public void setTimezone() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
		log.info("KuringApplication TimeZone = {}", TimeZone.getDefault());
	}

	@Bean
	public ApplicationRunner startApiWatcher() {
		return args -> {
			kuApiWatcher.watchAndUpdateNotice();
			kuApiWatcher.watchAndUpdateStaff();
		};
	}
}
