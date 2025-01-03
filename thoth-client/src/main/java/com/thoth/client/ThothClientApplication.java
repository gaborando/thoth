package com.thoth.client;

import com.thoth.common.Svg2Jpeg;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ThothClientApplication implements CommandLineRunner {


	private static final Logger logger = LogManager.getLogger(ThothClientApplication.class);

	private final ClientService clientService;

	public ThothClientApplication(ClientService clientService) {
		this.clientService = clientService;
	}

	public static void main(String[] args) {
		SpringApplication.run(ThothClientApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		Svg2Jpeg.warmup();
		logger.info("Thoth Client Starting (v1.0.14)");
		logger.info("Thoth Client Registering");
		clientService.register();
		logger.info("Thoth Client Registered");

	}
}
