package com.bi.barfdog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaAuditing // JPA Auditing 활성화
@SpringBootApplication
public class BarfdogApplication {

	public static void main(String[] args) {
		SpringApplication.run(BarfdogApplication.class, args);
	}

}
