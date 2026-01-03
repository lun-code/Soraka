package com.hospital.Soraka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SorakaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SorakaApplication.class, args);
	}

}
