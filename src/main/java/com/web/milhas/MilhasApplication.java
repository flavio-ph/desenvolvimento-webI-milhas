package com.web.milhas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MilhasApplication {

	public static void main(String[] args) {
		SpringApplication.run(MilhasApplication.class, args);
	}

}
