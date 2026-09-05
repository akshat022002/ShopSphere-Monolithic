package com.akshat.shopsphere;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableCaching
@SpringBootApplication
public class ShopsphereApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopsphereApplication.class, args);
	}

}
