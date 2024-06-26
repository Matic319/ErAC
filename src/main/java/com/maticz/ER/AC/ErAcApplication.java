package com.maticz.ER.AC;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ErAcApplication {

	public static void main(String[] args) {
		SpringApplication.run(ErAcApplication.class, args);
	}

}
