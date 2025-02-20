package com.email;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EmailWriterApplication {

	public static void main(String[] args) {
		System.out.println("Application is running........");
		SpringApplication.run(EmailWriterApplication.class, args);
	}

}
