package com.starturn.engine;

import com.starturn.engine.multipart.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({FileStorageProperties.class})
public class StarturnEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(StarturnEngineApplication.class, args);
	}

}
