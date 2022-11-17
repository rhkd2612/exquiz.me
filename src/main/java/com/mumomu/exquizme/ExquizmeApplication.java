package com.mumomu.exquizme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJms
public class ExquizmeApplication {
	public static void main(String[] args) {
		SpringApplication.run(ExquizmeApplication.class, args);
	}
}
