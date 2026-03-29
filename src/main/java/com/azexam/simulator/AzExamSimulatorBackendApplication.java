package com.azexam.simulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring Boot entry point for the Azure Exam Simulator backend.
 */
@EnableScheduling
@SpringBootApplication
public class AzExamSimulatorBackendApplication {

	/**
	 * Starts the Spring Boot application.
	 *
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(AzExamSimulatorBackendApplication.class, args);
	}

}
