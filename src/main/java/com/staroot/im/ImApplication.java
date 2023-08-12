package com.staroot.im;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.security.Security;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.staroot.im")
public class ImApplication {

	public static void main(String[] args) {

		SpringApplication.run(ImApplication.class, args);

	}

}
