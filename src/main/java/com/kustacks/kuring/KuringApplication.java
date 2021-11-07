package com.kustacks.kuring;

import com.kustacks.kuring.kuapi.KuApiWatcher;
import com.kustacks.kuring.kuapi.KuStaffDTO;
import com.kustacks.kuring.kuapi.StaffDeptInfo;
import com.kustacks.kuring.kuapi.StaffScraper;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;
import java.util.List;

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

	@Bean
	public ApplicationRunner startApiWatcher() {
		return args -> {
//			kuApiWatcher.watchAndUpdateNotice();
			kuApiWatcher.watchAndUpdateStaff();
		};
	}
}
