package com.gaayong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GaayongApplication {

	public static void main(String[] args) {
		SpringApplication.run(GaayongApplication.class, args);
	}

}
