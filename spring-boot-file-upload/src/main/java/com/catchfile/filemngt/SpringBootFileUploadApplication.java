package com.catchfile.filemngt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.catchfile.filemngt.entity")
public class SpringBootFileUploadApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootFileUploadApplication.class, args);
	}

}
