package com.thoth.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ThothApplication {



	public static void main(String[] args) {
		System.out.println("Starting Thoth Server");
		SpringApplication.run(ThothApplication.class, args);
	}

}
