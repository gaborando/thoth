package com.thot.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ThotClientApplication implements CommandLineRunner {


	private static final Logger logger = LogManager.getLogger(ThotClientApplication.class);

	private final ClientService clientService;

	public ThotClientApplication(ClientService clientService) {
		this.clientService = clientService;
	}

	public static void main(String[] args) {
		SpringApplication.run(ThotClientApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		logger.info("Thot Client Starting");
		logger.info("Thot Client Registering");
		clientService.register();
		logger.info("Thot Client Registered");

	}
}
