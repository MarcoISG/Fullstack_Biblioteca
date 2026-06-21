package com.duoc.prestamos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PrestamoApplication {

	public static void main(String[] args) {
		SpringApplication.run(PrestamoApplication.class, args);
	}

}
