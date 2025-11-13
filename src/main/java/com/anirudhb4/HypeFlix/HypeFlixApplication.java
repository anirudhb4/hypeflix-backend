package com.anirudhb4.HypeFlix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HypeFlixApplication {

	public static void main(String[] args) {
        // 1. FORCE TLS 1.2 (Fixes protocol mismatch)
        System.setProperty("https.protocols", "TLSv1.2");
        // 2. FORCE IPv4 (Fixes "terminated handshake" on Windows/Home networks)
        System.setProperty("java.net.preferIPv4Stack", "true");
        SpringApplication.run(HypeFlixApplication.class, args);
	}

}
