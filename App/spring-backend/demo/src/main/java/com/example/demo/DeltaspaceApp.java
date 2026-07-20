package com.example.demo;

import com.example.demo.logger.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DeltaspaceApp {

	public static void main(String[] args) {

		Logger.init();
		SpringApplication.run(DeltaspaceApp.class, args);
	}

}
