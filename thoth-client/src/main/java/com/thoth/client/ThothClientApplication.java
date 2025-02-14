package com.thoth.client;

import com.thoth.client.service.ThothService;
import com.thoth.client.service.rest.RestThothService;
import com.thoth.common.Svg2Jpeg;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class ThothClientApplication implements CommandLineRunner {


	private static final Logger logger = LogManager.getLogger(ThothClientApplication.class);

	private final ThothService thothService;

	public ThothClientApplication(ThothService thothService) {
        this.thothService = thothService;
    }

	public static void main(String[] args) {
		Svg2Jpeg.warmup();
		SpringApplication.run(ThothClientApplication.class, args);
	}

	@Override
	public void run(String... args) {
		logger.info("Thoth Client Starting (v1.0.14)");
		logger.info("Thoth Client Registering");
		thothService.registerClient();
		logger.info("Thoth Client Registered");

		if (thothService instanceof RestThothService s) {
			s.startConsumption();
		}

	}
}
