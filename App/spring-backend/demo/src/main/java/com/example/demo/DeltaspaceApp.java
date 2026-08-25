package com.example.demo;

import com.example.demo.logger.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class DeltaspaceApp {

	public static void main(String[] args) {

		Logger.init();
		SpringApplication.run(DeltaspaceApp.class, args);
	}
}
