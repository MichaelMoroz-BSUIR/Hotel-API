package com.example.hotelapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Hotel API", version = "1.0", description = "RESTful API for hotel management"))
public class HotelApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(HotelApiApplication.class, args);
	}

}
