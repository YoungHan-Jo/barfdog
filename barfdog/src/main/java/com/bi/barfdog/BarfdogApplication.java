package com.bi.barfdog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaAuditing // JPA Auditing 활성화
@SpringBootApplication
public class BarfdogApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(BarfdogApplication.class, args);
	}


	// ===================================================================
	// ====================== war 배포 설정 시작 ===========================
	// ===================================================================
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(BarfdogApplication.class);
	}
	// =================================================================
	// ====================== war 배포 설정 끝 ===========================
	// =================================================================

}
