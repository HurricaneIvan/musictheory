package com.example.musictheory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class MusictheoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(MusictheoryApplication.class, args);
	}

}
