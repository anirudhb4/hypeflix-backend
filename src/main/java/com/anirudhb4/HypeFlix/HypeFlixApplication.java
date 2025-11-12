package com.anirudhb4.HypeFlix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HypeFlixApplication {

	public static void main(String[] args) {
		SpringApplication.run(HypeFlixApplication.class, args);
	}

}
