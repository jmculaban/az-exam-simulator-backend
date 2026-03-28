package com.azexam.simulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AzExamSimulatorBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(AzExamSimulatorBackendApplication.class, args);
	}

}
