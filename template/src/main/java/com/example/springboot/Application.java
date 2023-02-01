package com.example.springboot;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			// Configuration configuration = new Configuration();
			// configuration.basePath = "https://petstore3.swagger.io";
			// OkHttpClient client = new OkHttpClient();
			// ApiClientPet apiClientPet = new ApiClientPet(client, configuration);
			// List<Pet> pets = apiClientPet.findPetsByStatus("available");
			// System.out.println(pets.stream().map(x -> x.getName()).collect(Collectors.toList()));
		};
	}

}
