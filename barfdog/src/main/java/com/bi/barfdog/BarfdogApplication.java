package com.bi.barfdog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
public class BarfdogApplication {

	public static void main(String[] args) {
		SpringApplication.run(BarfdogApplication.class, args);
	}

}
