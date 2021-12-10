package com.kustacks.kuring;

import com.kustacks.kuring.kuapi.notice.NoticeUpdater;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class KuringApplication {

	@Autowired
	NoticeUpdater noticeUpdater;

	public static void main(String[] args) {
		SpringApplication.run(KuringApplication.class, args);
	}

	@PostConstruct
	public void setTimezone() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
		log.info("KuringApplication TimeZone = {}", TimeZone.getDefault());
	}
}
