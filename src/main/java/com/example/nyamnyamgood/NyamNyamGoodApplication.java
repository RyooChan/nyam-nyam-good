package com.example.nyamnyamgood;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class NyamNyamGoodApplication {

    public static void main(String[] args) {
        SpringApplication.run(NyamNyamGoodApplication.class, args);
    }

}
